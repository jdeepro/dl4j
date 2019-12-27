package cn.centipede.numpy;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import cn.centipede.numpy.Numpy.np;

import static cn.centipede.numpy.Numpy.ALL;

/**
 * try to simulate numpy NDArray
 * a little tricky, oops...
 * I hope everything goes well!
 * dl4j@2019/11
 */
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
        this(array.data(), array.dataIndex(), array.dimens());
    }

    public boolean isInt() {
        return _isInt;
    }

    Object data() {
        return _data;
    }

    public int size() {
        return _size;
    }

    public int ndim() {
        return _dimens.length;
    }

    public int[] dimens() {
        return _dimens;
    }

    public int asInt() {
        if (_dimens == null || _dimens.length == 0) {
            int d = (int)_data;
            return d;
        } else {
            return ((int[])_data)[0];
        }
    }

    public double asDouble() {
        if (_dimens == null || _dimens.length == 0) {
            double d = (double)_data;
            return d;
        } else {
            return ((double[])_data)[0];
        }
    }

    /**
     * index data used to share data
     * @return index array
     */
    int[] dataIndex() {
        return _idata;
    }

    /**
     * Add support [newaxis, :], [:, newaxis]
     * support auto calc one dimen which is -1
     * for example: 12 => 3,-1 => 3, 4
     */
    public NDArray reshape(int ...dimens) {
        //[:, newaxis]
        if (dimens[0] == ALL) {
            _dimens = Arrays.copyOf(_dimens, _dimens.length+1);
            _dimens[_dimens.length-1] = 1;
            return this;
        }
        // dimens[0]==-1
        else if (dimens.length == 1) {
            _dimens = new int[]{_dimens.length};
            return this;
        }
        //[newaxis, :]
        else if (dimens[1] == ALL) {
            int[] axis = new int[_dimens.length+1];
            System.arraycopy(_dimens, 0, axis, 1, _dimens.length);
            _dimens = axis;
            _dimens[0] = 1;
            return this;
        }

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
        return new NDArray(_data, _idata, dimen);
    }

    /** common support int & double
     * data will be set to array
     * A = [2,3,4][5,6,7] => A[0]=1 => A=[1,1,1][5,6,7]
     * @param data number
     * @param index int[]
     */
    public NDArray set(Object data, int... index) {
        NDArray row = row(index[0]);
        if (index.length > 1) {
            row.set(data, Arrays.copyOfRange(index, 1, index.length));
            return this;
        }

        int[] idata = row.dataIndex();
        if (data instanceof NDArray) {
            NDArray array = (NDArray)data;
            int i = 0;
            data = np.getArrayData(array);
            for (int indx : idata) Array.set(_data, indx, Array.get(data, i++));
        } else {
            if (data.getClass().isArray()) {
                int i = 0;
                for (int indx : idata) Array.set(_data, indx, Array.get(data, i++));
            } else {
                for (int indx : idata) Array.set(_data, indx, data);
            }
        }
        return this;
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
        int step = arrays[0].dataIndex().length;
        int[] idata = new int[step*len];

        /** merge idata */
        for (int i = 0; i < arrays.length; i++) {
            NDArray array = arrays[i];
            System.arraycopy(array.dataIndex(), 0, idata, i*step, step);
        }

        int[] subDimens = arrays[0].dimens();
        int[] dimens = new int[subDimens.length+1];
        dimens[0] = arrays.length;
        System.arraycopy(subDimens, 0, dimens, 1, subDimens.length);
        return new NDArray(_data, idata, dimens);
    }

    /**
     * Split the NDArray to rows, and select one with index
     * @param row
     * @return sub NDArray
     */
    public NDArray row(int row) {
        if (row == ALL) {
            return this;
        }

        int length   = _idata.length/_dimens[0];
        int start    = row * length;
        int[] idata  = Arrays.copyOfRange(_idata, start, start+length);

        int[] dimens;
        if (_dimens.length == 2) {
            //dimens = Arrays.copyOf(_dimens, _dimens.length);
            dimens = new int[]{_dimens[1]};//1;
        } else {
            dimens = Arrays.copyOfRange(_dimens, 1, _dimens.length);
        }
        return new NDArray(_data, idata, dimens);
    }

    /**
     * get rows from range(start, stop)
     */
    public NDArray[] rows(int start, int stop) {
        NDArray[] arrays = new NDArray[stop-start];
        for (int i = 0; i < arrays.length; i++) {
            arrays[i] = row(i+start);
        }
        return arrays;
    }

    /**
     * get rows from range(start, stop)
     */
    public NDArray rows(int[] rows) {
        NDArray[] arrays = new NDArray[rows.length];
        for (int i = 0; i < arrays.length; i++) {
            arrays[i] = row(rows[i]);
        }
        return rows.length>1?merge(arrays):arrays[0];
    }

    /**
     * get data by index
     */
    private NDArray _index(int[] index) {
        NDArray[] arrays = new NDArray[_dimens[0]];

        for (int i = 0; i < _dimens[0]; i++) {
            NDArray array = row(i);
            arrays[i] = index[0]==ALL?array:array.index(index[0]);
        }

        if (index.length == 1) {
            return merge(arrays);
        }

        NDArray[] child = new NDArray[arrays.length];
        int[] subIndex = Arrays.copyOfRange(index, 1, index.length);

        for (int i = 0; i < arrays.length; i++) {
            if (index[0] == ALL) {
                child[i] = arrays[i].index(index);
            } else {
                child[i] = arrays[i].index(subIndex);
            }
        }
        return merge(child);
    }

    /**
     * Get data from the NDArray, eg:
     * index = a[2][3][4]
     * select whole rows/cols deeply
     * support negtive index
     * @param index select rows/cols block
     */
    public NDArray index(int... index) {
        /** update negtive index to positive */
        for (int i = 0; i < index.length; i++) {
            if (index[i] >= 0 || index[i] == ALL) {
                continue;
            }
            index[i] += _dimens[i];
        }

        /** only one row */
        if (index.length == 1) {
            return row(index[0]);
        }

        /** rows sub row */
        if (index[0] == ALL) {
            return _index(Arrays.copyOfRange(index, 1, index.length));
        }

        /** one sub array */
        NDArray array = row(index[0]);
        return array.index(Arrays.copyOfRange(index, 1, index.length));
    }

    /**
     * clip NDArray by range index
     * @param range
     * @return sub NDArray block
     */
    private NDArray _slice(int[][] range) {
        /** one slice to select rows */
        if (range.length == 1) {
            return range[0].length > 1 ?
                merge(rows(range[0][0], range[0][1])) : row(range[0][0]); // this is an array!
        }

        int[][] left = Arrays.copyOfRange(range, 1, range.length);

        /** select a row to slice */
        if (range[0].length == 1) {
            return row(range[0][0])._slice(left);
        }

        /** select rows to slice */
        NDArray[] array = rows(range[0][0], range[0][1]);
        NDArray[] ret = new NDArray[array.length];

        for (int i = 0; i < array.length; i++) {
            ret[i] = array[i]._slice(left);
        }

        /** merge all row slices */
        return merge(ret);
    }

    /**
     * do array slice like numpy
     * [3:5], [-2] etc...
     * int[][] range = {{1,4}, {2}, {2,5}};
     * @param range
     * @return slice array
     */
    public NDArray slice(int[][] range) {
        for (int i = 0; i < range.length; i++) {
            if (range[i][0] == ALL) {
                range[i] = new int[]{0, _dimens[i]};
                continue;
            } else if (range[i][0] < 0) {
                range[i][0] += _dimens[i];
            }
            if (range[i].length < 2) {
                continue;
            }
            if (range[i][1] == ALL) {
                range[i][1] = _dimens[i];
                continue;
            }
            if (range[i][1] < 0) {
                range[i][1] += _dimens[i];
            }
        }
        return _slice(range);
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

	public void dump() {
        System.out.println();
        System.out.println("shape=" + shape());
        System.out.println("array=");
        System.out.println(this);
	}
}
