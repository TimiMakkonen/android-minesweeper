package com.timimakkonen.minesweeper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import androidx.annotation.ColorInt;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * This class displays a minesweeper grid and allows users to interact on it.
 * </p>
 * <p>
 * To display the symbols of the minesweeper game, symbols for the corresponding symbols have to be
 * set via their setters or attributes.
 * </p>
 * <p>
 * Grid lines color, normal cell background color, and checked cell background color decide on the
 * main colours used for the lines and surfaces of the grid. These colours can be overridden by
 * their setters or attributes.
 * </p>
 * <p>
 * Grid line width automatically adjusts to the size of the grid, but it can also be overridden via
 * setter or attribute. To return back to default/automatic grid line width, the custom grid line
 * width must be unset using {@link #unsetGridLineStrokeWidth()}.
 * </p>
 * <p>
 * This class has the ability to return what it considers to be the maximum number of cells it can
 * display in a row or column via methods {@link #maxGridHeight()} and {@link #maxGridWidth()} ()},
 * respectively. To give even more flexibility about the recommended maximal sizes of the grid,
 * there are also other maximal methods. Namely: {@link #clickableMaxGridHeight()}, {@link
 * #reasonableMaxGridHeight()}, {@link #unreasonableMaxGridHeight()}, and their width equivalents.
 * {@link #clickableMaxGridHeight()} is suitable for unzoomed grids, {@link
 * #reasonableMaxGridHeight()} is suitable for zoomed grids and {@link #unreasonableMaxGridHeight()}
 * is just ridiculous. Currently {@link #maxGridHeight()} and {@link #reasonableMaxGridHeight()} are
 * equivalent, and the same applies to their width relatives.
 * </p>
 * <p>
 * To set the cell data needed to visualise the minesweeper grid, an instance of
 * 'VisualMinesweeperCell[][]' must be provided via {@link #setVisualMinesweeperCellsAndResize(VisualMinesweeperCell[][])}.
 * Alternatively you can also use {@link #setVisualMinesweeperCells(VisualMinesweeperCell[][])}, but
 * in this case you must also manually modify number of rows and columns displayed using {@link
 * #setNumberOfRows(int)} and {@link #setNumberOfColumns(int)}, respectively.
 * </p>
 * <p>
 * This minesweeper view can handle standard short(primary) and long(secondary) press touch events.
 * To use this, an implementation of {@link OnMinesweeperGridViewEventListener} must be set via
 * {@link #addMinesweeperEventListener(OnMinesweeperGridViewEventListener)}.
 * </p>
 * <p>
 * This view has zooming and panning/scrolling functionality, which should behave as expected.
 * </p>
 */
@SuppressWarnings({"unused"})
public class MinesweeperGridView extends View {

    private static final String TAG = "MinesweeperGridView";

    // static constants:
    private static final int DEFAULT_NUM_OF_COLUMNS = 10;
    private static final int DEFAULT_NUM_OF_ROWS = 13;
    private static final float DEFAULT_GRID_LINE_STROKE_WIDTH = 3;
    private static final float NULL_GRID_LINE_STROKE_WIDTH = -1;
    // minesweeper grid event listeners:
    private final List<MinesweeperGridView.OnMinesweeperGridViewEventListener>
            mMinesweeperGridViewEventListeners = new ArrayList<>();
    // grid size fields:
    private int mNumOfColumns = DEFAULT_NUM_OF_COLUMNS;
    private int mNumOfRows = DEFAULT_NUM_OF_ROWS;
    // color fields:
    @ColorInt
    private int mCellBgColor = Color.LTGRAY;
    @ColorInt
    private int mCheckedCellBgColor = Color.GRAY;
    @ColorInt
    private int mGridLinesColor = Color.BLACK;
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
    private float mGridLineStrokeWidth = DEFAULT_GRID_LINE_STROKE_WIDTH;
    private boolean mUseCustomGridLineStrokeWidth = false;
    // canvas rectangles and matrices:
    private RectF mContentRect = new RectF();
    private Matrix mContentMatrix = new Matrix();
    private Rect mGridRect = new Rect();
    private RectF mCurrentViewportRect = new RectF();
    private Matrix mCurrentViewMatrix = new Matrix();
    // gesture detector:
    private GestureDetector mGestureDetector;
    // scale gesture detector:
    private ScaleGestureDetector mScaleGestureDetector;

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

                e.transform(inverseOfMatrix(mCurrentViewMatrix));

                int eventX = (int) e.getX();
                int eventY = (int) e.getY();
                if (mGridRect.contains(eventX, eventY)) {

                    int clickedColumn = (eventX - mGridRect.left) / mCellSize;
                    int clickedRow = (eventY - mGridRect.top) / mCellSize;

                    Log.d(TAG, String.format("Primary cell action on (%d, %d)", clickedColumn,
                                             clickedRow));
                    dispatchMinesweeperPrimaryActionEvent(clickedColumn, clickedRow);
                }

                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                                    float distanceY) {
                mCurrentViewMatrix.postTranslate(-distanceX, -distanceY);
                Log.d(TAG, String.format("onScroll: xDist: %f, yDist: %f", distanceX, distanceY));

                calculateCurrentViewPortRectFromMatrix();

                Log.d(TAG, String.format(
                        "onScroll: BEFORE: CurrentViewportRect(%f, %f, %f, %f), ContentRect(%f, %f, %f, %f)",
                        mCurrentViewportRect.left, mCurrentViewportRect.top,
                        mCurrentViewportRect.right, mCurrentViewportRect.bottom, mContentRect.left,
                        mContentRect.top, mContentRect.right, mContentRect.bottom));

                validateAndCorrectViewPort();
                Log.d(TAG, String.format(
                        "onScroll: AFTER: CurrentViewportRect(%f, %f, %f, %f), ContentRect(%f, %f, %f, %f)",
                        mCurrentViewportRect.left, mCurrentViewportRect.top,
                        mCurrentViewportRect.right, mCurrentViewportRect.bottom, mContentRect.left,
                        mContentRect.top, mContentRect.right, mContentRect.bottom));

                invalidate();
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                Log.d(TAG, "GestureDetector: onLongPress");

                e.transform(inverseOfMatrix(mCurrentViewMatrix));

                int eventX = (int) e.getX();
                int eventY = (int) e.getY();
                if (mGridRect.contains(eventX, eventY)) {

                    int clickedColumn = (eventX - mGridRect.left) / mCellSize;
                    int clickedRow = (eventY - mGridRect.top) / mCellSize;

                    Log.d(TAG, String.format("Secondary cell action on (%d, %d)", clickedColumn,
                                             clickedRow));
                    dispatchMinesweeperSecondaryActionEvent(clickedColumn, clickedRow);
                }
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                   float velocityY) {
                return false;
            }
        });

        mScaleGestureDetector =
                new ScaleGestureDetector(
                        context,
                        new ScaleGestureDetector.SimpleOnScaleGestureListener() {

                            @Override
                            public boolean onScale(ScaleGestureDetector detector) {
                                final float scaleFactor = detector.getScaleFactor();
                                float newWidth =
                                        mCurrentViewportRect.width() / scaleFactor;
                                float newHeight =
                                        mCurrentViewportRect.height() / scaleFactor;

                                mCurrentViewMatrix.postScale(scaleFactor,
                                                             scaleFactor,
                                                             detector.getFocusX(),
                                                             detector.getFocusY());

                                calculateCurrentViewPortRectFromMatrix();
                                validateAndCorrectViewPort();

                                invalidate();

                                return true;
                            }
                        });
    }

    private void dispatchMinesweeperPrimaryActionEvent(int x, int y) {
        for (OnMinesweeperGridViewEventListener listener : mMinesweeperGridViewEventListeners) {
            listener.onCellPrimaryAction(x, y);
        }
    }

    private void dispatchMinesweeperSecondaryActionEvent(int x, int y) {
        for (OnMinesweeperGridViewEventListener listener : mMinesweeperGridViewEventListeners) {
            listener.onCellSecondaryAction(x, y);
        }
    }

    private void calculateCurrentViewMatrixFromViewportRect() {
        float scaleX = mContentRect.width() / mCurrentViewportRect.width();
        float scaleY = mContentRect.height() / mCurrentViewportRect.height();
        mCurrentViewMatrix = new Matrix();
        if (scaleX != 0 && scaleY != 0) {
            mCurrentViewMatrix.postScale(scaleX, scaleY);
            mCurrentViewMatrix.postTranslate(mContentRect.left - scaleX * mCurrentViewportRect.left,
                                             mContentRect.top - scaleY * mCurrentViewportRect.top);
        }
    }

    private void calculateCurrentViewPortRectFromMatrix() {
        float[] rectCorners =
                new float[]{mContentRect.left, mContentRect.top, mContentRect.right, mContentRect.bottom};
        inverseOfMatrix(mCurrentViewMatrix).mapPoints(rectCorners);
        mCurrentViewportRect.set(rectCorners[0], rectCorners[1], rectCorners[2], rectCorners[3]);
    }

    private Matrix inverseOfMatrix(Matrix matrixToInverse) {
        Matrix inverseMatrix = new Matrix();
        matrixToInverse.invert(inverseMatrix);
        return inverseMatrix;
    }

    private void validateAndCorrectViewPort() {
        if (!mContentRect.contains(mCurrentViewportRect)) {
            // if viewport rectangle is wider than content rectangle, reset viewport to content
            if (mCurrentViewportRect.width() > mContentRect.width()) {
                mCurrentViewportRect.set(mContentRect);
            }

            // horizontal translate if viewport outside content
            if (mCurrentViewportRect.left < mContentRect.left) {
                float delta = mContentRect.left - mCurrentViewportRect.left;
                mCurrentViewportRect.left += delta;
                mCurrentViewportRect.right += delta;
            } else if (mCurrentViewportRect.right > mContentRect.right) {
                float delta = mCurrentViewportRect.right - mContentRect.right;
                mCurrentViewportRect.left -= delta;
                mCurrentViewportRect.right -= delta;
            }

            // vertical translate if viewport outside content
            if (mCurrentViewportRect.top < mContentRect.top) {
                float delta = mContentRect.top - mCurrentViewportRect.top;
                mCurrentViewportRect.top += delta;
                mCurrentViewportRect.bottom += delta;
            } else if (mCurrentViewportRect.bottom > mContentRect.bottom) {
                float delta = mCurrentViewportRect.bottom - mContentRect.bottom;
                mCurrentViewportRect.top -= delta;
                mCurrentViewportRect.bottom -= delta;
            }

            Log.d(TAG, String.format(
                    "validateAndCorrectViewPort: BEFORE: CurrentViewportRect(%f, %f, %f, %f), ContentRect(%f, %f, %f, %f)",
                    mCurrentViewportRect.left, mCurrentViewportRect.top,
                    mCurrentViewportRect.right, mCurrentViewportRect.bottom, mContentRect.left,
                    mContentRect.top, mContentRect.right, mContentRect.bottom));

            calculateCurrentViewMatrixFromViewportRect();
            Log.d(TAG, String.format(
                    "validateAndCorrectViewPort: AFTER: CurrentViewportRect(%f, %f, %f, %f), ContentRect(%f, %f, %f, %f)",
                    mCurrentViewportRect.left, mCurrentViewportRect.top,
                    mCurrentViewportRect.right, mCurrentViewportRect.bottom, mContentRect.left,
                    mContentRect.top, mContentRect.right, mContentRect.bottom));
        }
    }

    private void loadAttributes(AttributeSet attrs, int defStyle) {

        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.MinesweeperGridView, defStyle, 0);

        loadSizeAttributes(a);

        loadColorAttributes(a);

        loadDrawableAttributes(a);

        a.recycle();
    }

    private void loadSizeAttributes(TypedArray a) {

        mNumOfRows = a.getInt(
                R.styleable.MinesweeperGridView_numberOfRows,
                DEFAULT_NUM_OF_ROWS);

        mNumOfColumns = a.getInt(
                R.styleable.MinesweeperGridView_numberOfColumns,
                DEFAULT_NUM_OF_COLUMNS);

        // if: grid line stroke width attribute is found, set it,
        // else: use default/'grid size dependant' stroke width
        float gridLineStrokeWidthAttr = a.getDimension(
                R.styleable.MinesweeperGridView_gridLineStrokeWidth,
                NULL_GRID_LINE_STROKE_WIDTH);
        if (gridLineStrokeWidthAttr != NULL_GRID_LINE_STROKE_WIDTH) {
            mGridLineStrokeWidth = gridLineStrokeWidthAttr;
            mUseCustomGridLineStrokeWidth = true;
        }
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
            if (mUncheckedDrawable != null) {
                mUncheckedDrawable.setCallback(this);
            }
        }

        // empty cell drawable:
        if (a.hasValue(R.styleable.MinesweeperGridView_emptyCellDrawable)) {
            mEmptyDrawable = a.getDrawable(
                    R.styleable.MinesweeperGridView_emptyCellDrawable);
            if (mEmptyDrawable != null) {
                mEmptyDrawable.setCallback(this);
            }
        }

        // one cell drawable:
        if (a.hasValue(R.styleable.MinesweeperGridView_oneCellDrawable)) {
            mOneDrawable = a.getDrawable(
                    R.styleable.MinesweeperGridView_oneCellDrawable);
            if (mOneDrawable != null) {
                mOneDrawable.setCallback(this);
            }
        }

        // two cell drawable:
        if (a.hasValue(R.styleable.MinesweeperGridView_twoCellDrawable)) {
            mTwoDrawable = a.getDrawable(
                    R.styleable.MinesweeperGridView_twoCellDrawable);
            if (mTwoDrawable != null) {
                mTwoDrawable.setCallback(this);
            }
        }

        // three cell drawable:
        if (a.hasValue(R.styleable.MinesweeperGridView_threeCellDrawable)) {
            mThreeDrawable = a.getDrawable(
                    R.styleable.MinesweeperGridView_threeCellDrawable);
            if (mThreeDrawable != null) {
                mThreeDrawable.setCallback(this);
            }
        }

        // four cell drawable:
        if (a.hasValue(R.styleable.MinesweeperGridView_fourCellDrawable)) {
            mFourDrawable = a.getDrawable(
                    R.styleable.MinesweeperGridView_fourCellDrawable);
            if (mFourDrawable != null) {
                mFourDrawable.setCallback(this);
            }
        }

        // five cell drawable:
        if (a.hasValue(R.styleable.MinesweeperGridView_fiveCellDrawable)) {
            mFiveDrawable = a.getDrawable(
                    R.styleable.MinesweeperGridView_fiveCellDrawable);
            if (mFiveDrawable != null) {
                mFiveDrawable.setCallback(this);
            }
        }

        // six cell drawable:
        if (a.hasValue(R.styleable.MinesweeperGridView_sixCellDrawable)) {
            mSixDrawable = a.getDrawable(
                    R.styleable.MinesweeperGridView_sixCellDrawable);
            if (mSixDrawable != null) {
                mSixDrawable.setCallback(this);
            }
        }

        // seven cell drawable:
        if (a.hasValue(R.styleable.MinesweeperGridView_sevenCellDrawable)) {
            mSevenDrawable = a.getDrawable(
                    R.styleable.MinesweeperGridView_sevenCellDrawable);
            if (mSevenDrawable != null) {
                mSevenDrawable.setCallback(this);
            }
        }

        // eight cell drawable:
        if (a.hasValue(R.styleable.MinesweeperGridView_eightCellDrawable)) {
            mEightDrawable = a.getDrawable(
                    R.styleable.MinesweeperGridView_eightCellDrawable);
            if (mEightDrawable != null) {
                mEightDrawable.setCallback(this);
            }
        }

        // mine cell drawable:
        if (a.hasValue(R.styleable.MinesweeperGridView_mineCellDrawable)) {
            mMineDrawable = a.getDrawable(
                    R.styleable.MinesweeperGridView_mineCellDrawable);
            if (mMineDrawable != null) {
                mMineDrawable.setCallback(this);
            }
        }

        // marked cell drawable:
        if (a.hasValue(R.styleable.MinesweeperGridView_markedCellDrawable)) {
            mMarkedDrawable = a.getDrawable(
                    R.styleable.MinesweeperGridView_markedCellDrawable);
            if (mMarkedDrawable != null) {
                mMarkedDrawable.setCallback(this);
            }
        }
    }

    private void invalidatePaintColors() {
        mCellBgPaint.setColor(mCellBgColor);
        mCheckedCellBgPaint.setColor(mCheckedCellBgColor);
        mGridLinesPaint.setColor(mGridLinesColor);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        invalidateDimensions();
    }

    private void invalidateDimensions() {

        final int viewWidth = getWidth();
        final int viewHeight = getHeight();
        final int paddingLeft = getPaddingLeft();
        final int paddingTop = getPaddingTop();
        final int paddingRight = getPaddingRight();
        final int paddingBottom = getPaddingBottom();

        mContentRect.set(paddingLeft, paddingTop, viewWidth - paddingRight,
                         viewHeight - paddingTop);

        // set initial viewport rectangle and matrix
        mCurrentViewportRect.set(mContentRect);
        calculateCurrentViewMatrixFromViewportRect();

        // if explicit/custom grid line stroke width has not been set,
        // use the following stroke width
        if (!mUseCustomGridLineStrokeWidth) {
            // grid line stroke width is the minimum between 'default' and 'estimated cellSize / 8'
            mGridLineStrokeWidth = Math.min(DEFAULT_GRID_LINE_STROKE_WIDTH,
                                            Math.min(mContentRect.width() / mNumOfColumns,
                                                     mContentRect.height() / mNumOfRows) / 16);
            mGridLinesPaint.setStrokeWidth(mGridLineStrokeWidth);
        }

        // Check if width or height is the limiting cell size factor and set cell size and grid
        // rectangle accordingly. The grid rectangle is centered inside content rectangle.
        final int maximalCellWidth =
                (int) ((mContentRect.width() - mGridLineStrokeWidth) / mNumOfColumns);
        final int maximalCellHeight =
                (int) ((mContentRect.height() - mGridLineStrokeWidth) / mNumOfRows);
        mCellSize = Math.min(maximalCellHeight, maximalCellWidth);
        final int gridWidth = mCellSize * mNumOfColumns;
        final int gridHeight = mCellSize * mNumOfRows;
        final int gridLeft = (int) ((mContentRect.left + mContentRect.right - gridWidth) / 2);
        final int gridTop = (int) ((mContentRect.top + mContentRect.bottom - gridHeight) / 2);
        mGridRect.set(gridLeft, gridTop, gridLeft + gridWidth, gridTop + gridHeight);

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.clipRect(mContentRect);

        canvas.save();
        if (mCurrentViewMatrix != null) {
            canvas.setMatrix(mCurrentViewMatrix);
        }

        // to avoid some unnecessary drawing (overdraw), we will only draw cells close to viewport
        final int minRow = Math.max(
                (int) ((mCurrentViewportRect.top - mGridRect.top) / mCellSize) - 1,
                0);
        final int minColumn = Math.max(
                (int) ((mCurrentViewportRect.left - mGridRect.left) / mCellSize) - 1,
                0);
        final int maxRow = Math.min(
                (int) ((mCurrentViewportRect.bottom - mGridRect.top) / mCellSize) + 1,
                mNumOfRows);
        final int maxColumn = Math.min(
                (int) ((mCurrentViewportRect.right - mGridRect.left) / mCellSize) + 1,
                mNumOfColumns);

        drawGridCells(canvas, minRow, minColumn, maxRow, maxColumn);

        drawGridLines(canvas, minRow, minColumn, maxRow, maxColumn);
        canvas.restore();
    }

    private void drawGridCells(Canvas canvas, int minRow, int minColumn, int maxRow,
                               int maxColumn) {

        for (int y = minRow; y < maxRow; ++y) {
            for (int x = minColumn; x < maxColumn; ++x) {
                drawCell(canvas, x, y);
            }
        }
    }

    private void drawCell(Canvas canvas, int x, int y) {
        Rect cellBounds = new Rect(mGridRect.left + (x * mCellSize),
                                   mGridRect.top + (y * mCellSize),
                                   mGridRect.left + ((x + 1) * mCellSize),
                                   mGridRect.top + ((y + 1) * mCellSize));
        if (mVisualMinesweeperCells != null) {
            switch (mVisualMinesweeperCells[y][x]) {
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

    private void drawGridLines(Canvas canvas, int minRow, int minColumn, int maxRow,
                               int maxColumn) {
        // draw column/vertical lines:
        for (int x = minColumn; x <= maxColumn; ++x) {
            canvas.drawLine(mGridRect.left + (x * mCellSize), mGridRect.top,
                            mGridRect.left + (x * mCellSize),
                            mGridRect.bottom, mGridLinesPaint);
        }

        // draw row/horizontal lines:
        for (int y = minRow; y <= maxRow; ++y) {
            canvas.drawLine(mGridRect.left, mGridRect.top + (y * mCellSize),
                            mGridRect.right,
                            mGridRect.top + (y * mCellSize), mGridLinesPaint);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean retVal = mScaleGestureDetector.onTouchEvent(event);
        retVal = mGestureDetector.onTouchEvent(event) || retVal;
        return retVal || super.onTouchEvent(event);
    }

    public int maxGridHeight() {
        return this.reasonableMaxGridHeight();
    }

    public int maxGridWidth() {
        return this.reasonableMaxGridWidth();
    }

    public int clickableMaxGridHeight() {
        return (int) (mContentRect.height() /
                      ((float) (24 * getContext().getResources().getDisplayMetrics().densityDpi) /
                       DisplayMetrics.DENSITY_DEFAULT));
    }

    public int clickableMaxGridWidth() {
        return (int) (mContentRect.width() /
                      ((float) (24 * getContext().getResources().getDisplayMetrics().densityDpi) /
                       DisplayMetrics.DENSITY_DEFAULT));
    }

    public int reasonableMaxGridHeight() {
        return (int) (mContentRect.height() / 16);
    }

    public int reasonableMaxGridWidth() {
        return (int) (mContentRect.width() / 16);
    }

    public int unreasonableMaxGridHeight() {
        return (int) (mContentRect.height() / 2);
    }

    public int unreasonableMaxGridWidth() {
        return (int) (mContentRect.width() / 2);
    }

    //================================================================================
    // Getters and Setters:
    //================================================================================

    /**
     * Gets the number of columns on the grid.
     *
     * @return The number of columns on the grid.
     */
    public int getNumberOfColumns() {
        return mNumOfColumns;
    }

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

    public void setCellBgColor(@ColorInt int mCellBgColor) {
        this.mCellBgColor = mCellBgColor;
        invalidatePaintColors();
    }

    public int getGridLinesColor() {
        return mGridLinesColor;
    }

    public void setGridLinesColor(@ColorInt int mGridLinesColor) {
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

    public float getGridLineStrokeWidth() {
        return this.mGridLineStrokeWidth;
    }

    public void setGridLineStrokeWidth(float gridLineStrokeWidth) {
        this.mGridLineStrokeWidth = gridLineStrokeWidth;
        this.mUseCustomGridLineStrokeWidth = true;
        invalidateDimensions();
    }

    public void unsetGridLineStrokeWidth() {
        this.mUseCustomGridLineStrokeWidth = false;
        invalidateDimensions();
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
     * Sets the mVisualMinesweeperCells to draw/visualise the minesweeper grid. Also sets the number
     * of rows (mNumOfRows) and columns (mNumOfColumns) to keep these consistent with the
     * mVisualMinesweeperCells.
     *
     * @param visualMinesweeperCells VisualMinesweeperCells to draw/visualise.
     * @throws IllegalArgumentException if Rows of the VisualMinesweeperCell[][] are not same
     *                                  length.
     */
    public void setVisualMinesweeperCellsAndResize(VisualMinesweeperCell[][] visualMinesweeperCells)
            throws IllegalArgumentException {

        Log.d(TAG, "setVisualMinesweeperCellsAndResize: Setting visual minesweeper cells");
        this.mVisualMinesweeperCells = visualMinesweeperCells;
        int newNumOfRows = visualMinesweeperCells.length;
        int newNumOfColumns = 0;
        if (newNumOfRows > 0) {
            newNumOfColumns = visualMinesweeperCells[0].length;

            // check lengths of rows are consistent
            for (VisualMinesweeperCell[] row : visualMinesweeperCells) {
                if (row.length != newNumOfColumns) {
                    throw new IllegalArgumentException(
                            "Rows of the VisualMinesweeperCell[][] are not same length!");
                }
            }
        }

        int oldNumOfRows = this.mNumOfRows;
        int oldNumOfColumns = this.mNumOfColumns;
        this.mNumOfRows = newNumOfRows;
        this.mNumOfColumns = newNumOfColumns;

        if (oldNumOfRows != newNumOfRows || oldNumOfColumns != newNumOfColumns) {
            invalidateDimensions();
        }

        invalidate();
    }

    public void addMinesweeperEventListener(
            MinesweeperGridView.OnMinesweeperGridViewEventListener listener) {
        this.mMinesweeperGridViewEventListeners.add(listener);
    }

    public void removeMinesweeperEventListener(
            MinesweeperGridView.OnMinesweeperGridViewEventListener listener) {
        this.mMinesweeperGridViewEventListeners.remove(listener);
    }

    public void clearMinesweeperEventListeners() {
        this.mMinesweeperGridViewEventListeners.clear();
    }

    public interface OnMinesweeperGridViewEventListener {
        void onCellPrimaryAction(int x, int y);

        void onCellSecondaryAction(int x, int y);
    }

}
