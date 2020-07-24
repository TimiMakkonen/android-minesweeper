package com.timimakkonen.minesweeper;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;


public class MinesweeperGridView extends View {

    private static final String TAG = "MinesweeperGridView";

    // static constants:
    private static final int DEFAULT_NUM_OF_COLUMNS = 10;
    private static final int DEFAULT_NUM_OF_ROWS = 13;
    private static final float DEFAULT_GRID_LINE_STROKE_WIDTH = 3;

    // grid size fields:
    private int mNumOfColumns = DEFAULT_NUM_OF_COLUMNS; // TODO: use a default from R.dimen...
    private int mNumOfRows = DEFAULT_NUM_OF_ROWS; // TODO: use a default from R.dimen...

    // color fields:
    private int mCellBgColor = Color.LTGRAY; // TODO: use a default from R.color...
    private int mCheckedCellBgColor = Color.GRAY; // TODO: use a default from R.color...
    private int mGridLinesColor = Color.BLACK; // TODO: use a default from R.color...

    // drawable symbols:
    private Drawable mUncheckedDrawable;
    private Drawable mEmptyDrawable;
    private Drawable mOneDrawable;
    private Drawable mTwoDrawable;
    private Drawable mThreeDrawable;
    private Drawable mFourDrawable;
    private Drawable mFiveDrawable;
    private Drawable mSixDrawable;
    private Drawable mSevenDrawable;
    private Drawable mEightDrawable;
    private Drawable mMineDrawable;
    private Drawable mFlagDrawable;
    private Drawable mMarkedDrawable;

    // Minesweeper cells:
    private VisualMinesweeperCell[][] mVisualMinesweeperCells;

    // grid paints:
    private Paint mCellBgPaint;
    private Paint mCheckedCellBgPaint;
    private Paint mGridLinesPaint;

    // sizes:
    private int mCellSize;
    private int mViewWidth;
    private int mViewHeight;
    private int mPaddingLeft;
    private int mPaddingTop;
    private int mPaddingRight;
    private int mPaddingBottom;
    private int mContentWidth;
    private int mContentHeight;
    private int mGridWidth;
    private int mGridHeight;
    private float mGridLineStrokeWidth = DEFAULT_GRID_LINE_STROKE_WIDTH;

    // grid origin point on canvas;
    private int mOriginX;
    private int mOriginY;

    // gesture detector:
    private GestureDetector mGestureDetector;

    // minesweeper grid event listener
    private OnMinesweeperGridViewEventListener mMinesweeperGridViewEventListener;

    public MinesweeperGridView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public MinesweeperGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public MinesweeperGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {

        loadAttributes(attrs, defStyle);

        // Set up a default paint objects
        mCellBgPaint = new Paint();
        mCellBgPaint.setStyle(Paint.Style.FILL);
        mCheckedCellBgPaint = new Paint();
        mCheckedCellBgPaint.setStyle(Paint.Style.FILL);
        mGridLinesPaint = new Paint();
        mGridLinesPaint.setStrokeWidth(mGridLineStrokeWidth);

        invalidatePaintColors();

        mGestureDetector = new GestureDetector(context, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                Log.d(TAG, "GestureDetector: onSingleTapUp");

                int eventX = (int) e.getX();
                int eventY = (int) e.getY();
                if (eventX >= mOriginX && eventY >= mOriginY && eventX < mOriginX + mGridWidth &&
                    eventY < mOriginY + mGridHeight) {

                    int clickedColumn = (eventX - mOriginX) / mCellSize;
                    int clickedRow = (eventY - mOriginY) / mCellSize;

                    Log.d(TAG, String.format("Checking cell (%d, %d)", clickedColumn, clickedRow));
                    mMinesweeperGridViewEventListener.onCellCheck(clickedColumn, clickedRow);
                }

                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                                    float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                Log.d(TAG, "GestureDetector: onLongPress");

                int eventX = (int) e.getX();
                int eventY = (int) e.getY();
                if (eventX >= mOriginX && eventY >= mOriginY && eventX < mOriginX + mGridWidth &&
                    eventY < mOriginY + mGridHeight) {

                    int clickedColumn = (eventX - mOriginX) / mCellSize;
                    int clickedRow = (eventY - mOriginY) / mCellSize;

                    Log.d(TAG, String.format("Marking cell (%d, %d)", clickedColumn, clickedRow));
                    mMinesweeperGridViewEventListener.onCellMark(clickedColumn, clickedRow);
                }
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                   float velocityY) {
                return false;
            }
        });

    }

    private void loadAttributes(AttributeSet attrs, int defStyle) {

        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.MinesweeperGridView, defStyle, 0);


        mGridLineStrokeWidth = a.getDimension(
                R.styleable.MinesweeperGridView_gridLineStrokeWidth,
                mGridLineStrokeWidth);

        loadColorAttributes(a);

        loadDrawableAttributes(a);

        a.recycle();
    }

    private void loadColorAttributes(TypedArray a) {
        mCellBgColor = a.getColor(
                R.styleable.MinesweeperGridView_cellBgColor,
                mCellBgColor);

        mCheckedCellBgColor = a.getColor(
                R.styleable.MinesweeperGridView_checkedCellBgColor,
                mCheckedCellBgColor);

        mGridLinesColor = a.getColor(
                R.styleable.MinesweeperGridView_gridLinesColor,
                mGridLinesColor);
    }

    private void loadDrawableAttributes(TypedArray a) {

        // unchecked cell drawable:
        if (a.hasValue(R.styleable.MinesweeperGridView_uncheckedCellDrawable)) {
            mUncheckedDrawable = a.getDrawable(
                    R.styleable.MinesweeperGridView_uncheckedCellDrawable);
            mUncheckedDrawable.setCallback(this);
        }

        // empty cell drawable:
        if (a.hasValue(R.styleable.MinesweeperGridView_emptyCellDrawable)) {
            mEmptyDrawable = a.getDrawable(
                    R.styleable.MinesweeperGridView_emptyCellDrawable);
            mEmptyDrawable.setCallback(this);
        }

        // one cell drawable:
        if (a.hasValue(R.styleable.MinesweeperGridView_oneCellDrawable)) {
            mOneDrawable = a.getDrawable(
                    R.styleable.MinesweeperGridView_oneCellDrawable);
            mOneDrawable.setCallback(this);
        }

        // two cell drawable:
        if (a.hasValue(R.styleable.MinesweeperGridView_twoCellDrawable)) {
            mTwoDrawable = a.getDrawable(
                    R.styleable.MinesweeperGridView_twoCellDrawable);
            mTwoDrawable.setCallback(this);
        }

        // three cell drawable:
        if (a.hasValue(R.styleable.MinesweeperGridView_threeCellDrawable)) {
            mThreeDrawable = a.getDrawable(
                    R.styleable.MinesweeperGridView_threeCellDrawable);
            mThreeDrawable.setCallback(this);
        }

        // four cell drawable:
        if (a.hasValue(R.styleable.MinesweeperGridView_fourCellDrawable)) {
            mFourDrawable = a.getDrawable(
                    R.styleable.MinesweeperGridView_fourCellDrawable);
            mFourDrawable.setCallback(this);
        }

        // five cell drawable:
        if (a.hasValue(R.styleable.MinesweeperGridView_fiveCellDrawable)) {
            mFiveDrawable = a.getDrawable(
                    R.styleable.MinesweeperGridView_fiveCellDrawable);
            mFiveDrawable.setCallback(this);
        }

        // six cell drawable:
        if (a.hasValue(R.styleable.MinesweeperGridView_sixCellDrawable)) {
            mSixDrawable = a.getDrawable(
                    R.styleable.MinesweeperGridView_sixCellDrawable);
            mSixDrawable.setCallback(this);
        }

        // seven cell drawable:
        if (a.hasValue(R.styleable.MinesweeperGridView_sevenCellDrawable)) {
            mSevenDrawable = a.getDrawable(
                    R.styleable.MinesweeperGridView_sevenCellDrawable);
            mSevenDrawable.setCallback(this);
        }

        // eight cell drawable:
        if (a.hasValue(R.styleable.MinesweeperGridView_eightCellDrawable)) {
            mEightDrawable = a.getDrawable(
                    R.styleable.MinesweeperGridView_eightCellDrawable);
            mEightDrawable.setCallback(this);
        }

        // mine cell drawable:
        if (a.hasValue(R.styleable.MinesweeperGridView_mineCellDrawable)) {
            mMineDrawable = a.getDrawable(
                    R.styleable.MinesweeperGridView_mineCellDrawable);
            mMineDrawable.setCallback(this);
        }

        // marked cell drawable:
        if (a.hasValue(R.styleable.MinesweeperGridView_markedCellDrawable)) {
            mMarkedDrawable = a.getDrawable(
                    R.styleable.MinesweeperGridView_markedCellDrawable);
            mMarkedDrawable.setCallback(this);
        }
    }

    private void invalidatePaintColors() {
        mCellBgPaint.setColor(mCellBgColor);
        mCheckedCellBgPaint.setColor(mCheckedCellBgColor);
        mGridLinesPaint.setColor(mGridLinesColor);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        invalidateDimensions();
    }

    private void invalidateDimensions() {
        // TODO
        mViewWidth = getWidth();
        mViewHeight = getHeight();
        mPaddingLeft = getPaddingLeft();
        mPaddingTop = getPaddingTop();
        mPaddingRight = getPaddingRight();
        mPaddingBottom = getPaddingBottom();

        mContentWidth = mViewWidth - mPaddingLeft - mPaddingRight;
        mContentHeight = mViewHeight - mPaddingTop - mPaddingBottom;

        final int maximalCellWidth = mContentWidth / mNumOfColumns;
        final int maximalCellHeight = mContentHeight / mNumOfRows;

        // check if width or height is the limiting cell size factor
        // and set cell size, grid size and the origin of the grid accordingly
        // origin is the top left corner of the grid and is adjusted to centralize the grid
        if (maximalCellWidth < maximalCellHeight) {
            mCellSize = maximalCellWidth;
            mGridWidth = mCellSize * mNumOfColumns;
            mGridHeight = mCellSize * mNumOfRows;
            mOriginX = mPaddingLeft;
            mOriginY = mPaddingTop + (mContentHeight - mGridHeight) / 2;
        } else {
            mCellSize = maximalCellHeight;
            mGridWidth = mCellSize * mNumOfColumns;
            mGridHeight = mCellSize * mNumOfRows;
            mOriginX = mPaddingLeft + (mContentWidth - mGridWidth) / 2;
            mOriginY = mPaddingTop;
        }

        mCellSize = Math.min(mContentWidth / mNumOfColumns, mContentHeight / mNumOfRows);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // draw grid cells
        for (int y = 0; y < mNumOfRows; ++y) {
            for (int x = 0; x < mNumOfColumns; ++x) {
                drawCell(canvas, x, y);
            }
        }

        drawGridLines(canvas);
    }

    private void drawCell(Canvas canvas, int x, int y) {
        Rect cellBounds = new Rect(mOriginX + (x * mCellSize), mOriginY + (y * mCellSize),
                                   mOriginX + ((x + 1) * mCellSize),
                                   mOriginY + ((y + 1) * mCellSize));
        if (mVisualMinesweeperCells != null) {
            switch (mVisualMinesweeperCells[y][x]) { // TODO:
                case UNCHECKED:
                    canvas.drawRect(cellBounds, mCellBgPaint);
                    drawDrawableToCell(canvas, mUncheckedDrawable, cellBounds);
                    break;
                case EMPTY:
                    canvas.drawRect(cellBounds, mCheckedCellBgPaint);
                    drawDrawableToCell(canvas, mEmptyDrawable, cellBounds);
                    break;
                case ONE:
                    canvas.drawRect(cellBounds, mCheckedCellBgPaint);
                    drawDrawableToCell(canvas, mOneDrawable, cellBounds);
                    break;
                case TWO:
                    canvas.drawRect(cellBounds, mCheckedCellBgPaint);
                    drawDrawableToCell(canvas, mTwoDrawable, cellBounds);
                    break;
                case THREE:
                    canvas.drawRect(cellBounds, mCheckedCellBgPaint);
                    drawDrawableToCell(canvas, mThreeDrawable, cellBounds);
                    break;
                case FOUR:
                    canvas.drawRect(cellBounds, mCheckedCellBgPaint);
                    drawDrawableToCell(canvas, mFourDrawable, cellBounds);
                    break;
                case FIVE:
                    canvas.drawRect(cellBounds, mCheckedCellBgPaint);
                    drawDrawableToCell(canvas, mFiveDrawable, cellBounds);
                    break;
                case SIX:
                    canvas.drawRect(cellBounds, mCheckedCellBgPaint);
                    drawDrawableToCell(canvas, mSixDrawable, cellBounds);
                    break;
                case SEVEN:
                    canvas.drawRect(cellBounds, mCheckedCellBgPaint);
                    drawDrawableToCell(canvas, mSevenDrawable, cellBounds);
                    break;
                case EIGHT:
                    canvas.drawRect(cellBounds, mCheckedCellBgPaint);
                    drawDrawableToCell(canvas, mEightDrawable, cellBounds);
                    break;
                case MINE:
                    canvas.drawRect(cellBounds, mCheckedCellBgPaint);
                    drawDrawableToCell(canvas, mMineDrawable, cellBounds);
                    break;
                case MARKED:
                    canvas.drawRect(cellBounds, mCellBgPaint);
                    drawDrawableToCell(canvas, mMarkedDrawable, cellBounds);
                    break;
            }
        } else {
            canvas.drawRect(cellBounds, mCellBgPaint);
        }
    }

    private void drawDrawableToCell(Canvas canvas, Drawable drawable, Rect cellBounds) {
        if (drawable != null) {
            drawable.setBounds(cellBounds);
            drawable.draw(canvas);
        }
    }

    private void drawGridLines(Canvas canvas) {
        // draw column/vertical lines:
        for (int i = 0; i <= mNumOfColumns; ++i) {
            canvas.drawLine(mOriginX + (i * mCellSize), mOriginY, mOriginX + (i * mCellSize),
                            mOriginY + mGridHeight, mGridLinesPaint);
        }

        // draw row/horizontal lines:
        for (int i = 0; i <= mNumOfRows; ++i) {
            canvas.drawLine(mOriginX, mOriginY + (i * mCellSize), mOriginX + mGridWidth,
                            mOriginY + (i * mCellSize), mGridLinesPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        boolean result = mGestureDetector.onTouchEvent(event);
        return result;
    }

    public int maxGridHeight() {
        return 30;
    }

    public int maxGridWidth() {
        return 20;
    }

    /**
     * Gets the number of columns on the grid.
     *
     * @return The number of columns on the grid.
     */
    public int getNumberOfColumns() {
        return mNumOfColumns;
    }

    //================================================================================
    // Getters and Setters:
    //================================================================================

    /**
     * Sets the number of columns attribute value.
     *
     * @param numOfColumns The number of columns to use on the grid.
     */
    public void setNumberOfColumns(int numOfColumns) {
        this.mNumOfColumns = numOfColumns;
        invalidateDimensions();
    }

    /**
     * Gets the number of rows on the grid.
     *
     * @return The number of rows on the grid.
     */
    public int getNumberOfRows() {
        return mNumOfRows;
    }

    /**
     * Sets the number of rows attribute value.
     *
     * @param numOfRows The number of rows to use on the grid.
     */
    public void setNumberOfRows(int numOfRows) {
        this.mNumOfRows = numOfRows;
        invalidateDimensions();
    }

    public int getCellBgColor() {
        return mCellBgColor;
    }

    public void setCellBgColor(int mCellBgColor) {
        this.mCellBgColor = mCellBgColor;
        invalidatePaintColors();
    }

    public int getGridLinesColor() {
        return mGridLinesColor;
    }

    public void setGridLinesColor(int mGridLinesColor) {
        this.mGridLinesColor = mGridLinesColor;
        invalidatePaintColors();
    }

    public Drawable getEmptyDrawable() {
        return mEmptyDrawable;
    }

    public void setEmptyDrawable(Drawable mEmptyDrawable) {
        this.mEmptyDrawable = mEmptyDrawable;
    }

    public Drawable getOneDrawable() {
        return mOneDrawable;
    }

    public void setOneDrawable(Drawable mOneDrawable) {
        this.mOneDrawable = mOneDrawable;
    }

    public Drawable getTwoDrawable() {
        return mTwoDrawable;
    }

    public void setTwoDrawable(Drawable mTwoDrawable) {
        this.mTwoDrawable = mTwoDrawable;
    }

    public Drawable getThreeDrawable() {
        return mThreeDrawable;
    }

    public void setThreeDrawable(Drawable mThreeDrawable) {
        this.mThreeDrawable = mThreeDrawable;
    }

    public Drawable getFourDrawable() {
        return mFourDrawable;
    }

    public void setFourDrawable(Drawable mFourDrawable) {
        this.mFourDrawable = mFourDrawable;
    }

    public Drawable getFiveDrawable() {
        return mFiveDrawable;
    }

    public void setFiveDrawable(Drawable mFiveDrawable) {
        this.mFiveDrawable = mFiveDrawable;
    }

    public Drawable getSixDrawable() {
        return mSixDrawable;
    }

    public void setSixDrawable(Drawable mSixDrawable) {
        this.mSixDrawable = mSixDrawable;
    }

    public Drawable getSevenDrawable() {
        return mSevenDrawable;
    }

    public void setSevenDrawable(Drawable mSevenDrawable) {
        this.mSevenDrawable = mSevenDrawable;
    }

    public Drawable getEightDrawable() {
        return mEightDrawable;
    }

    public void setEightDrawable(Drawable mEightDrawable) {
        this.mEightDrawable = mEightDrawable;
    }

    public Drawable getMineDrawable() {
        return mMineDrawable;
    }

    public void setMineDrawable(Drawable mMineDrawable) {
        this.mMineDrawable = mMineDrawable;
    }

    public Drawable getFlagDrawable() {
        return mFlagDrawable;
    }

    public void setFlagDrawable(Drawable mFlagDrawable) {
        this.mFlagDrawable = mFlagDrawable;
    }

    public Drawable getUncheckedDrawable() {
        return mUncheckedDrawable;
    }

    public void setUncheckedDrawable(Drawable mUncheckedDrawable) {
        this.mUncheckedDrawable = mUncheckedDrawable;
    }

    public Drawable getMarkedDrawable() {
        return mMarkedDrawable;
    }

    public void setMarkedDrawable(Drawable mMarkedDrawable) {
        this.mMarkedDrawable = mMarkedDrawable;
    }

    /**
     * Calls {@link #setVisualMinesweeperCellsAndResize(VisualMinesweeperCell[][])}.
     *
     * @param mVisualMinesweeperCells VisualMinesweeperCells to draw/visualise.
     */
    public void setVisualMinesweeperCells(VisualMinesweeperCell[][] mVisualMinesweeperCells) {
        setVisualMinesweeperCellsAndResize(mVisualMinesweeperCells);
    }

    /**
     * Sets the mVisualMinesweeperCells to draw/visualise the minesweeper grid.
     * Also sets the number of rows (mNumOfRows) and columns (mNumOfColumns) to keep these consistent
     * with the mVisualMinesweeperCells.
     *
     * @param visualMinesweeperCells VisualMinesweeperCells to draw/visualise.
     * @throws IllegalArgumentException if Rows of the VisualMinesweeperCell[][] are not same length.
     */
    public void setVisualMinesweeperCellsAndResize(VisualMinesweeperCell[][] visualMinesweeperCells)
            throws IllegalArgumentException {

        Log.d(TAG, "setVisualMinesweeperCellsAndResize: Setting visual minesweeper cells");
        this.mVisualMinesweeperCells = visualMinesweeperCells;
        this.mNumOfRows = visualMinesweeperCells.length;
        if (this.mNumOfRows > 0) {
            this.mNumOfColumns = visualMinesweeperCells[0].length;

            // check lengths of rows are consistent
            for (VisualMinesweeperCell[] row :
                    visualMinesweeperCells) {
                if (row.length != this.mNumOfColumns) {
                    throw new IllegalArgumentException(
                            "Rows of the VisualMinesweeperCell[][] are not same length!");
                }
            }
        } else {
            this.mNumOfColumns = 0;
        }

        invalidateDimensions();
        invalidate();
    }

    public void setMinesweeperGridViewEventListener(
            OnMinesweeperGridViewEventListener minesweeperGridViewEventListener) {
        this.mMinesweeperGridViewEventListener = minesweeperGridViewEventListener;
    }

    public interface OnMinesweeperGridViewEventListener {
        void onCellCheck(int x, int y);

        void onCellMark(int x, int y);
    }
}
