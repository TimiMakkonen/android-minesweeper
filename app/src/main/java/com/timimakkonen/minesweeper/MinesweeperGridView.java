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
 * Grid line width, which has default size set by 'DEFAULT_GRID_LINE_STROKE_WIDTH' can also be
 * overridden via setter or attribute.
 * </p>
 * <p>
 * This class has the ability to return what it considers to be the maximum number of cells it can
 * display in a row or column via methods {@link #maxGridHeight()} and {@link #maxGridWidth()} ()},
 * respectively.
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
 */
@SuppressWarnings({"unused"})
public class MinesweeperGridView extends View {

    private static final String TAG = "MinesweeperGridView";

    // static constants:
    private static final int DEFAULT_NUM_OF_COLUMNS = 10;
    private static final int DEFAULT_NUM_OF_ROWS = 13;
    private static final float DEFAULT_GRID_LINE_STROKE_WIDTH = 3;
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
    // content rectangle:
    private RectF mContentRect = new RectF();
    private Matrix mContentMatrix = new Matrix();
    private Rect mGridRect = new Rect();
    // TODO: Add scaling/zooming and scrolling/panning
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

                            //private float lastSpanX;
                            //private float lastSpanY;

                            @Override
                            public boolean onScale(ScaleGestureDetector detector) {
                                //float spanX = detector.getCurrentSpanX();
                                //float spanY = detector.getCurrentSpanY();
                                //float span = detector.getCurrentSpan();

                                //float newWidth = lastSpanX / spanX * mCurrentViewportRect.width();
                                //float newHeight = lastSpanY / spanY * mCurrentViewportRect.height();
                                float newWidth =
                                        mCurrentViewportRect.width() / detector.getScaleFactor();
                                float newHeight =
                                        mCurrentViewportRect.height() / detector.getScaleFactor();
                                //float newWidth

                                //detector.getCurrentSpan()

                                //                                float focusX = detector.getFocusX();
                                //                                float focusY = detector.getFocusY();
                                //                                PointF viewportFocus = new PointF(
                                //                                        mCurrentViewportRect.left + mCurrentViewportRect.width() *
                                //                                                                    (focusX - mContentRect.left) /
                                //                                                                    mContentRect.width(),
                                //                                        mCurrentViewportRect.top + mCurrentViewportRect.height() *
                                //                                                                   (focusY - mContentRect.bottom) /
                                //                                                                   -mContentRect.height());

                                mCurrentViewMatrix.postScale(detector.getScaleFactor(),
                                                             detector.getScaleFactor(),
                                                             detector.getFocusX(),
                                                             detector.getFocusY());

                                //                                mCurrentViewportRect
                                //                                        .set(viewportFocus.x -
                                //                                             newWidth * (focusX - mContentRect.left) /
                                //                                             mContentRect.width(),
                                //                                             viewportFocus.y -
                                //                                             newHeight * (mContentRect.bottom - focusY) /
                                //                                             mContentRect.height(),
                                //                                             0,
                                //                                             0);
                                //
                                //                                mCurrentViewportRect.right = mCurrentViewportRect.left + newWidth;
                                //                                mCurrentViewportRect.bottom = mCurrentViewportRect.top + newHeight;

                                //lastSpanX = spanX;
                                //lastSpanY = spanY;

                                //calculateCurrentViewMatrixFromViewportRect();

                                //                                Log.d(TAG, String.format(
                                //                                        "onScale: CurrentViewPort is now: (%f, %f. %f, %f)",
                                //                                        mCurrentViewportRect.left, mCurrentViewportRect.top,
                                //                                        mCurrentViewportRect.right, mCurrentViewportRect.bottom));

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
        //float scaleX = mContentRect.width() / getWidth();
        //float scaleY = mContentRect.height() / getHeight();
        mCurrentViewMatrix = new Matrix();
        if (scaleX != 0 && scaleY != 0) {
            //mCurrentViewMatrix.postTranslate(-mContentRect.left - mCurrentViewportRect.left,
            //                                 -mContentRect.top - mCurrentViewportRect.top);
            //mCurrentViewMatrix.postTranslate(-mCurrentViewportRect.left, mCurrentViewportRect.top);
            mCurrentViewMatrix.postScale(scaleX, scaleY);
            mCurrentViewMatrix.postTranslate(mContentRect.left - scaleX * mCurrentViewportRect.left,
                                             mContentRect.top - scaleY * mCurrentViewportRect.top);
        }
    }

    private void calculateCurrentViewPortRectFromMatrix() {
        //float[] rectCorners = new float[]{0, 0, getWidth(), getHeight()};
        float[] rectCorners =
                new float[]{mContentRect.left, mContentRect.top, mContentRect.right, mContentRect.bottom};
        //Matrix currentViewMatrixInverse = new Matrix();
        Matrix currentViewMatrixInverse = new Matrix(mCurrentViewMatrix);
        //mCurrentViewMatrix.invert(currentViewMatrixInverse);
        currentViewMatrixInverse.invert(currentViewMatrixInverse);
        currentViewMatrixInverse.mapPoints(rectCorners);
        mCurrentViewportRect.set(rectCorners[0], rectCorners[1], rectCorners[2], rectCorners[3]);
    }

    private Matrix inverseOfMatrix(Matrix matrixToInverse) {
        Matrix inverseMatrix = new Matrix();
        matrixToInverse.invert(inverseMatrix);
        return inverseMatrix;
    }

    private void validateAndCorrectViewPort() {
        if (!mContentRect.contains(mCurrentViewportRect)) {
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
            // TODO: delete next line
            calculateCurrentViewPortRectFromMatrix();

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

        //mContentMatrix = new Matrix();
        //mContentMatrix.postTranslate(-mContentRect.left, -mContentRect.top);
        //mContentMatrix.postScale(mContentRect.width()/viewWidth, mContentRect.height()/viewHeight);


        mCurrentViewportRect.set(mContentRect);
        calculateCurrentViewMatrixFromViewportRect();

        final int maximalCellWidth =
                (int) ((mContentRect.width() - mGridLineStrokeWidth) / mNumOfColumns);
        final int maximalCellHeight =
                (int) ((mContentRect.height() - mGridLineStrokeWidth) / mNumOfRows);

        // check if width or height is the limiting cell size factor
        // and set cell size, grid size and the origin of the grid accordingly
        // origin is the top left corner of the grid and is adjusted to centralize the grid
//        if (maximalCellWidth < maximalCellHeight) { // TODO(Timi)
//            mCellSize = maximalCellWidth;
//            final int gridWidth = mCellSize * mNumOfColumns;
//            final int gridHeight = mCellSize * mNumOfRows;
//            final int gridLeft;
//            final int gridTop = (int) ((mContentRect.top + mContentRect.bottom - gridHeight) / 2);
//
//            mGridRect.set(paddingLeft, gridTop, paddingLeft + gridWidth,
//                          gridTop + gridHeight);
//        } else {
//            mCellSize = maximalCellHeight;
//            final int gridWidth = mCellSize * mNumOfColumns;
//            final int gridHeight = mCellSize * mNumOfRows;
//            final int originX = (int) (paddingLeft + (mContentRect.width() - gridWidth) / 2);
//
//            mGridRect.set(originX, paddingTop, originX + gridWidth,
//                          paddingTop + gridHeight);
//        }

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

        // draw grid cells
        for (int y = 0; y < mNumOfRows; ++y) {
            for (int x = 0; x < mNumOfColumns; ++x) {
                drawCell(canvas, x, y);
            }
        }

        drawGridLines(canvas);
        canvas.restore();
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

    private void drawGridLines(Canvas canvas) {
        // draw column/vertical lines:
        for (int i = 0; i <= mNumOfColumns; ++i) {
            canvas.drawLine(mGridRect.left + (i * mCellSize), mGridRect.top,
                            mGridRect.left + (i * mCellSize),
                            mGridRect.bottom, mGridLinesPaint);
        }

        // draw row/horizontal lines:
        for (int i = 0; i <= mNumOfRows; ++i) {
            canvas.drawLine(mGridRect.left, mGridRect.top + (i * mCellSize),
                            mGridRect.right,
                            mGridRect.top + (i * mCellSize), mGridLinesPaint);
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
        return (int) (mContentRect.height() /
                      ((float) (24 * getContext().getResources().getDisplayMetrics().densityDpi) /
                       DisplayMetrics.DENSITY_DEFAULT));
    }

    public int maxGridWidth() {
        return (int) (mContentRect.width() /
                      ((float) (24 * getContext().getResources().getDisplayMetrics().densityDpi) /
                       DisplayMetrics.DENSITY_DEFAULT));
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

    public void setGridLineStrokeWidth(float gridLineStrokeWidth) {
        this.mGridLineStrokeWidth = gridLineStrokeWidth;
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
