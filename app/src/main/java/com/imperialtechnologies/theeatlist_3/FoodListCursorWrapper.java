package com.imperialtechnologies.theeatlist_3;

import android.database.Cursor;
import android.database.CursorWrapper;

public class FoodListCursorWrapper extends CursorWrapper {
    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    private String filter;
    private int column;
    private int[] index;
    private int count;
    private int pos;

    /** filters on the column number passed in constructor using string filter */
    public FoodListCursorWrapper(Cursor cursor, String filter, int column) {
        super(cursor);

        //Initialize variables
        this.filter = filter.toLowerCase();
        this.column = column;

        //The pos variable is used in the index wrapper to refer to cursor position i
        this.pos = 0;

        this.count = super.getCount();
        this.index = new int[this.count];

        if (this.filter != "") {

            for (int i = 0; i < this.count; i++) {
                super.moveToPosition(i);
                //Log.d("Wrapper", "Row#: " + Integer.toString(i) + " Value: " + this.getString(2));
                if (this.getString(this.column).toLowerCase().contains(this.filter))
                    this.index[this.pos++] = i;
            }

            this.count = this.pos;
            this.pos = 0;
            super.moveToFirst();

        } else {

            for (int i = 0; i < this.count; i++) {
                this.index[i] = i;
            }
        }

    }

    @Override
    public boolean move(int offset) {
        return this.moveToPosition(this.pos+offset);
    }

    @Override
    public boolean moveToNext() {
        return this.moveToPosition(this.pos+1);
    }

    @Override
    public boolean moveToPrevious() {
        return this.moveToPosition(this.pos-1);
    }

    @Override
    public boolean moveToFirst() {
        return this.moveToPosition(0);
    }

    @Override
    public boolean moveToLast() {
        return this.moveToPosition(this.count-1);
    }

    @Override
    public boolean moveToPosition(int position) {
        if (position >= this.count || position < 0)
            return false;
        return super.moveToPosition(this.index[position]);
    }

    @Override
    public int getCount() {
        return this.count;
    }

    @Override
    public int getPosition() {
        return this.pos;
    }

}
