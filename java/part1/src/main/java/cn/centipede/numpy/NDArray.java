package cn.centipede.numpy;

import java.lang.reflect.Array;
import java.util.Arrays;

import static cn.centipede.numpy.Numpy.ALL;


public class NDArray implements Cloneable{
    private Object _data;
    private int[]  _idata;
    private int[]  _dimens;
    private int    _size;
    private boolean _isInt;


    public NDArray(Object data, int... dimens) {
        _data   = data;
        _dimens = dimens;
        _size   = dimens.length>0?Array.getLength(data):1;
        _idata  = new int[_size];

        for (int i = 0; i < _size; i++) {
            _idata[i] = i;
        }
        _isInt = data instanceof int[];
    }

    public NDArray(Object data, int[] idata, int... dimens) {
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
        return this;
    }

    public int ndim() {
        return _dimens.length;
    }

    public int[] getDimens() {
        return _dimens;
    }

    public NDArray T() {
        int[] idataR = Numpy.transpose(_idata, _dimens);
        int[] dimensR = ArrayHelper.reverse(_dimens);
        return new NDArray(_data, idataR, dimensR);
    }

    /** common support int & double
     * data will be set to array
     * A = [2,3,4][5,6,7] => A[0]=1 => A=[1,1,1][5,6,7]
     * @param data number
     * @param index int[]
     */
    public void set(Object data, int... index) {
        if (index.length > 1) {
            NDArray row = getTopRow(index[0]);
            row.set(data, Arrays.copyOfRange(index, 1, index.length));
        } else {
            int[] idata = getTopRow(index[0]).getDataIndex();
            for (int indx : idata) {
                Array.set(_data, indx, data);
            }
        }
    }

    /**
     * the dimens shape of the data
     * @return (2,3)
     */
    public String shape() {
        StringBuilder sb = new StringBuilder("(");
        sb.append(_dimens[0]);
        for (int i = 1; i < _dimens.length; i++) {
            sb.append(", ");
            sb.append(_dimens[i]);
        }
        return sb.append(")").toString();
    }

    /**
     * index data used to share data
     * @return index array
     */
    int[] getDataIndex() {
        return _idata;
    }

    private int[] getDataIndex(int parts, int index) {
        int size   = _idata.length;
        int length = size/parts;
        int start  = index * length;
        return Arrays.copyOfRange(_idata, start, start+length);
    }


    private NDArray merge(NDArray[] arrays) {
        int len = arrays.length;
        int step = arrays[0].getDataIndex().length;
        int size = step*len;
        int[] idata = new int[size];

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
     * TODO:support deep row
     * @param row
     * @return
     */
    public NDArray getTopRow(int row) {
        if (row == ALL) {
            return this;
        }
        int[] idata = getDataIndex(_dimens[0], row);
        int[] dimens = Arrays.copyOfRange(_dimens, 1, _dimens.length);
        return new NDArray(_data, idata, dimens);
    }

    public NDArray[] getTopRowRange(int start, int stop) {
        NDArray[] arrays = new NDArray[stop-start];
        for (int i = 0; i < arrays.length; i++) {
            arrays[i] = getTopRow(i+start);
        }
        return arrays;
    }

    private NDArray getRows(int[] index) {
        int[] subIndex = Arrays.copyOfRange(index, 1, index.length);
        NDArray[] arrays = new NDArray[_dimens[0]];

        for (int i = 0; i < _dimens[0]; i++) {
            NDArray array = getTopRow(i);
            arrays[i] = index[0]==ALL?array:array.at(index[0]);
        }

        if (index.length == 1) {
            return merge(arrays);
        }

        NDArray[] child = new NDArray[arrays.length];
        for (int i = 0; i < arrays.length; i++) {
            if (index[0] == ALL) {
                child[i] = arrays[i].at(index);
            } else {
                child[i] = arrays[i].at(subIndex);
            }
        }
        return merge(child);
    }

    public NDArray at(int... index) {
        for (int i = 0; i < index.length; i++) {
            if (index[i] != ALL && index[i] < 0) {
                index[i] += _dimens[i];
            }
        }

        if (index.length == 1) {
            return getTopRow(index[0]);
        }

        if (index[0] == ALL) {
            return getRows(Arrays.copyOfRange(index, 1, index.length));
        } else {
            NDArray array = getTopRow(index[0]);
            return array.at(Arrays.copyOfRange(index, 1, index.length));
        }
    }

    public NDArray atRange(int[]... index) {
        int count = 0;
        for (int[] ints : index) {
            if (ints != null) count++;
        }

        if (count == 1) {
            if (index[0].length>1) {
                return merge(getTopRowRange(index[0][0], index[0][1]));
            } else {
                return getTopRow(index[0][0]);
            }
        }

        NDArray[] array = getTopRowRange(index[0][0], index[0][1]);
        NDArray[] ret = new NDArray[array.length];
        for (int i = 0; i < array.length; i++) {
            ret[i] = array[i].atRange(Arrays.copyOfRange(index, 1, index.length));
        }
        return merge(ret);
    }

    public NDArray slice(int[][] range) {
        for (int i = 0; i < range.length; i++) {
            if (range[i] == null) {
                continue;
            }
            if (range[i][0] == ALL) {
                range[i] = new int[]{0, _dimens[0]};
            } else if (range[i][0] < 0) {
                range[i][0] += _dimens[i];
            }

            if (range[i].length>1 && range[i][1] != ALL && range[i][1] < 0) {
                range[i][1] += _dimens[i];
            }
        }
        return atRange(range);
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
        return ArrayHelper.getString(_data, _dimens, _idata);
    }
}
