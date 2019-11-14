package cn.centipede.numpy;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static cn.centipede.numpy.Numpy.ALL;


public class NDArray implements Cloneable{
    private Object _data;
    private int[]  _idata;
    private int[]  _dimens;
    private int    _size;
    private boolean _isInt;

    /** Transpose */
    public NDArray T;


    public NDArray(Object data, int... dimens) {
        if (data instanceof NDArray) {
            _data = ((NDArray)data)._data;
        } else {
            _data = data;
        }

        _dimens = dimens;
        if (dimens.length == 0 && data.getClass().isArray()) {
            _size = Array.getLength(_data);
        } else {
            _size   = dimens.length>0?Array.getLength(_data):1;
        }

        _idata  = new int[_size];

        for (int i = 0; i < _size; i++) {
            _idata[i] = i;
        }

        _isInt = _data instanceof int[];
        T = _dimens.length > 1?_T():this;
    }

    public NDArray(Object data, int[] idata, int... dimens) {
        _data   = data;
        _dimens = dimens;
        _size   = idata.length;
        _idata  = idata;
        _isInt = data instanceof int[];

        if (_dimens.length > 1) {
            T = _T();
        }
    }

    /**
     * inner use!!
     * for Transpose .T
     */
    private NDArray(int[] idata, Object data, int... dimens) {
        _data   = data;
        _dimens = dimens;
        _size   = idata.length;
        _idata  = idata;
        _isInt = data instanceof int[];
    }

    public NDArray(NDArray array) {
        this(array.data(), array.getDataIndex(), array.dimens());
    }

    private int[] dimens() {
        return _dimens;
    }

    public boolean isInt() {
        return _isInt;
    }

    Object data() {
        return _data;
    }

    public int ndim() {
        return _dimens.length;
    }

    public int[] getDimens() {
        return _dimens;
    }

    public double asDouble() {
        if (_dimens == null || _dimens.length == 0) {
            double d = (double)_data;
            return d;
        } else {
            double d = (Double)Array.get(_data, 0);
            return d;
        }
    }

    /**
     * index data used to share data
     * @return index array
     */
    int[] getDataIndex() {
        return _idata;
    }

    /**
     * support auto calc one dimen which is -1
     * for example: 12 => 3,-1 => 3, 4
     */
    public NDArray reshape(int ...dimens) {
        int size = _size, pos = -1;

        for (int i = 0; i < dimens.length; i++) {
            if (dimens[i] == -1) pos = i;
            else size /= dimens[i];
        }

        if (pos != -1) {
            dimens[pos] = size;
        }
        _dimens = dimens;
        T = _T();
        return this;
    }

    private NDArray _T() {
        int[] idataR = Numpy.transpose(_idata, _dimens);
        int[] dimensR = ArrayHelper.reverse(_dimens);
        return new NDArray(idataR, _data, dimensR);
    }

    public NDArray V() {
        int[] dimen = new int[]{1, _size};
        return new NDArray(_data, dimen);
    }

    /** common support int & double
     * data will be set to array
     * A = [2,3,4][5,6,7] => A[0]=1 => A=[1,1,1][5,6,7]
     * @param data number
     * @param index int[]
     */
    public void set(Object data, int... index) {
        if (index.length > 1) {
            NDArray row = getRow(index[0]);
            row.set(data, Arrays.copyOfRange(index, 1, index.length));
        } else {
            int[] idata = getRow(index[0]).getDataIndex();
            for (int indx : idata) {
                Array.set(_data, indx, data);
            }
        }
    }

    /**
     * the dimens shape of the data
     * shape(NDArray(5))=>()
     * shape(NDArray(new int[]{5,7}))=>(2,)
     * @return (2,3)
     */
    public String shape() {
        if (_dimens.length == 0) {
            return "()"; // just one number
        }

        // array but not a vector
        if (_dimens.length == 1) {
            return "(" + _dimens[0] + ",)";
        }

        String join = IntStream.of(_dimens)
            .mapToObj(Integer::toString)
            .collect(Collectors.joining(", "));

        return "(" + join + ")";
    }

    /**
     * merge all same diemen arrays to be one NDArray
     * always used to slice some rows/parts of one NDArray.
     */
    private NDArray merge(NDArray[] arrays) {
        int len = arrays.length;
        int step = arrays[0].getDataIndex().length;
        int[] idata = new int[step*len];

        /** merge idata */
        for (int i = 0; i < arrays.length; i++) {
            NDArray array = arrays[i];
            System.arraycopy(array.getDataIndex(), 0, idata, i*step, step);
        }

        int[] subDimens = arrays[0].getDimens();
        int[] dimens = new int[subDimens.length+1];
        dimens[0] = arrays.length;
        System.arraycopy(subDimens, 0, dimens, 1, subDimens.length);
        return new NDArray(_data, idata, dimens);
    }

    /**
     * split ndarray to rows, and select one by row index
     * @param row
     * @return sub NDArray
     */
    public NDArray getRow(int row) {
        if (row == ALL) {
            return this;
        }

        int length   = _idata.length/_dimens[0];
        int start    = row * length;
        int[] idata  = Arrays.copyOfRange(_idata, start, start+length);
        int[] dimens = Arrays.copyOfRange(_dimens, 1, _dimens.length);
        return new NDArray(_data, idata, dimens);
    }

    /**
     * get rows from range(start, stop)
     */
    public NDArray[] getRows(int start, int stop) {
        NDArray[] arrays = new NDArray[stop-start];
        for (int i = 0; i < arrays.length; i++) {
            arrays[i] = getRow(i+start);
        }
        return arrays;
    }

    /**
     * get data by index
     */
    private NDArray getDataByIndex(int[] index) {
        NDArray[] arrays = new NDArray[_dimens[0]];

        for (int i = 0; i < _dimens[0]; i++) {
            NDArray array = getRow(i);
            arrays[i] = index[0]==ALL?array:array.at(index[0]);
        }

        if (index.length == 1) {
            return merge(arrays);
        }

        NDArray[] child = new NDArray[arrays.length];
        int[] subIndex = Arrays.copyOfRange(index, 1, index.length);

        for (int i = 0; i < arrays.length; i++) {
            if (index[0] == ALL) {
                child[i] = arrays[i].at(index);
            } else {
                child[i] = arrays[i].at(subIndex);
            }
        }
        return merge(child);
    }

    /**
     * to support negtive index
     * @param which
     * @param index
     */
    private void positiveIndex(int which, int[] index) {
        if (index[which] >= 0 || index[which] == ALL) {
            return;
        }
        index[which] += _dimens[which];
    }

    /**
     * to support negtive index
     * @param which
     * @param range
     */
    private int[] positiveRange(int which, int[] range) {
        if (range == null) {
            return range;
        }

        if (range[0] == ALL) {
            range = new int[]{0, _dimens[0]};
        } else if (range[0] < 0) {
            range[0] += _dimens[which];
        }

        if (range.length>1 && range[1] != ALL && range[1] < 0) {
            range[1] += _dimens[which];
        }
        return range;
    }

    /**
     * select whole rows/cols
     * support negtive index
     * @param index select rows/cols block
     */
    public NDArray at(int... index) {
        /** update negtive index to positive */
        for (int i = 0; i < index.length; i++) {
            positiveIndex(i, index);
        }

        /** only one row */
        if (index.length == 1) {
            return getRow(index[0]);
        }

        /** select all rows or one row */
        if (index[0] == ALL) {
            return getDataByIndex(Arrays.copyOfRange(index, 1, index.length));
        } else {
            NDArray array = getRow(index[0]);
            return array.at(Arrays.copyOfRange(index, 1, index.length));
        }
    }

    /**
     * clip NDArray by range index
     * @param range
     * @return sub NDArray block
     */
    public NDArray atRange(int[]... range) {
        /** one slice to select rows */
        if (range.length == 1) {
            return range[0].length > 1 ?
                merge(getRows(range[0][0], range[0][1])):
                getRow(range[0][0]); // this is an array!
        }

        int[][] left = Arrays.copyOfRange(range, 1, range.length);

        /** select a row to slice */
        if (range[0].length == 1) {
            return getRow(range[0][0]).atRange(left);
        }

        /** select rows to slice */
        NDArray[] array = getRows(range[0][0], range[0][1]);
        NDArray[] ret = new NDArray[array.length];
        for (int i = 0; i < array.length; i++) {
            ret[i] = array[i].atRange(left);
        }

        /** merge all row slices */
        return merge(ret);
    }

    /**
     * do array slice like numpy
     * @param range
     * @return slice array
     */
    public NDArray slice(int[][] range) {
        for (int i = 0; i < range.length; i++) {
            range[i] = positiveRange(i, range[i]);
        }
        return atRange(range);
    }

    public NDArray add(Object dat) {
        return Numpy.add(this, dat);
    }

    public NDArray subtract(Object dat) {
        return Numpy.subtract(this, dat);
    }

    public NDArray multiply(Object dat) {
        return Numpy.multiply(this, dat);
    }

    public NDArray dot(NDArray dat) {
        return Numpy.dot(this, dat);
    }

    public NDArray negative() {
        return Numpy.multiply(this, -1);
    }

    public NDArray divide(Object dat) {
        return Numpy.divide(this, dat);
    }

    public NDArray reciprocal() {
        return Numpy.reciprocal(this);
    }

    public boolean same(NDArray dst, double slope) {
        return Numpy.same(this, dst, slope);
    }

    @Override
    protected NDArray clone() {
        Object data = Array.newInstance(_isInt?int.class:double.class, _size);

        if (_isInt) for (int i = 0; i < _size; i++) {
            ((int[])data)[i] = ((int[])_data)[_idata[i]];
        } else for (int i = 0; i < _size; i++) {
            ((double[])data)[i] = ((double[])_data)[_idata[i]];
        }
        return new NDArray(data, _dimens);
    }

    @Override
    public String toString() {
        if (_dimens == null || _dimens.length == 0) {
            boolean isArray = _data.getClass().isArray();
            return isArray ? Array.get(_data, _idata[0]).toString():_data.toString();
        }
        return ArrayHelper.getString(_data, _dimens, _idata);
    }

    @Override
    public boolean equals(Object dst) {
        if (this == dst) {
            return true;
        }
        if (dst instanceof NDArray) {
            return Numpy.compare(this, (NDArray)dst);
        }
        return false;
    }
}
