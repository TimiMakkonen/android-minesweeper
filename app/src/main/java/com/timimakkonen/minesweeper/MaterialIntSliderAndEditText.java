package com.timimakkonen.minesweeper;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * This class is a composite view consisting of synchronised 'Material Slider' with discrete integer
 * values, and 'Material TextEdit' with number values. When either one has been edited, the other
 * one should update so that they represent the same value.
 * </p>
 * <p>
 * 'maxValue', 'minValue' and 'value/currentValue' can be set as expected via setters and
 * attributes.
 * </p>
 * <p>
 * Changes in the 'value/currentValue' can be listened with an implementation of
 * 'MaterialIntSliderAndEditText.OnChangeListener', which can be set using 'addOnChangeListener'
 * method.
 * </p>
 */
public class MaterialIntSliderAndEditText extends LinearLayout {

    private static final String TAG = "MatIntSliderAndEditT";

    private final static int DEFAULT_MIN_VALUE = 0;
    private final static int DEFAULT_MAX_VALUE = 0;
    private final static int DEFAULT_CURRENT_VALUE = 0;

    private final List<OnChangeListener> changeListeners = new ArrayList<>();
    private Slider slider;
    private TextInputEditText editText;
    private int maxValue;
    private int minValue;
    private int currentValue;


    public MaterialIntSliderAndEditText(Context context) {
        super(context);
        init(null, 0);
    }

    public MaterialIntSliderAndEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public MaterialIntSliderAndEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {

        loadAttributes(attrs, defStyle);

        inflate(getContext(), R.layout.material_int_slider_and_edittext, this);

        slider = findViewById(R.id.slider_materialintsliderandedittext);
        editText = findViewById(R.id.edittext_materialintsliderandedittext);

        setUpListeners();
    }

    private void loadAttributes(AttributeSet attrs, int defStyle) {

        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.MaterialIntSliderAndEditText, defStyle, 0);

        minValue = a.getInteger(R.styleable.MaterialIntSliderAndEditText_minValue,
                                DEFAULT_MIN_VALUE);

        maxValue = a.getInteger(R.styleable.MaterialIntSliderAndEditText_maxValue,
                                DEFAULT_MAX_VALUE);

        currentValue = a.getInteger(R.styleable.MaterialIntSliderAndEditText_value,
                                    DEFAULT_CURRENT_VALUE);

        a.recycle();
    }

    private void setUpListeners() {

        slider.addOnChangeListener((slider1, value, fromUser) -> {
            if (fromUser) {
                int valueToSet = (int) value;
                if (value < this.minValue) {
                    valueToSet = this.minValue;
                } else if (value > this.maxValue) {
                    valueToSet = this.maxValue;
                }
                updateCurrentValue(valueToSet);
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    int textValue = Integer.parseInt(s.toString());
                    if (textValue >= minValue && textValue <= maxValue) {
                        updateCurrentValue(textValue);
                    }
                } catch (NumberFormatException ex) {
                    Log.d(TAG, "setUpListeners: editTextListener: Invalid integer");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                try {
                    int textValue = Integer.parseInt(String.valueOf(editText.getText()));
                    updateCurrentValue(textValue);
                } catch (NumberFormatException ex) {
                    Log.d(TAG, "setUpListeners: editTextListener: Invalid integer");
                }
            }
        });
        editText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                actionId == EditorInfo.IME_ACTION_NEXT) {
                try {
                    int textValue = Integer.parseInt(String.valueOf(editText.getText()));
                    updateCurrentValue(textValue);
                } catch (NumberFormatException ex) {
                    Log.d(TAG, "setUpListeners: editTextListener: Invalid integer");
                }
            }
            return false;
        });
    }

    private void updateCurrentValue(int value) {
        if (value < minValue) {
            currentValue = minValue;
        } else {
            currentValue = Math.min(value, maxValue);
        }
        updateChildViewValues();
        dispatchOnChanged();
    }

    private void updateChildViewValues() {
        if (slider.getValue() != currentValue) {
            slider.setValue(currentValue);
        }
        String editTextValue = String.valueOf(editText.getText());
        if (!editTextValue.equals(String.valueOf(currentValue))) {
            editText.setText(String.valueOf(currentValue));
        }
    }

    private void dispatchOnChanged() {
        for (OnChangeListener listener : changeListeners) {
            listener.onValueChange(this, currentValue);
        }
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        if (maxValue < this.minValue) {
            throw new IllegalArgumentException(
                    String.format(
                            "maxvalue (%d) should not be smaller than minValue (%d)",
                            maxValue,
                            this.minValue));
        }

        this.maxValue = maxValue;
        if (currentValue > maxValue) {
            updateCurrentValue(maxValue);
        }

        setSliderRange();
    }

    private void setSliderRange() {

        if (this.minValue == this.maxValue) {
            slider.setValueFrom(this.minValue - 1);
            slider.setValueTo(this.maxValue + 1);
        } else {
            slider.setValueFrom(this.minValue);
            slider.setValueTo(this.maxValue);
        }
    }

    public int getMinValue() {
        return minValue;
    }

    public void setMinValue(int minValue) {
        if (minValue > this.maxValue) {
            throw new IllegalArgumentException(
                    String.format(
                            "minvalue (%d) should not be bigger than maxValue (%d)",
                            minValue,
                            this.maxValue));
        }

        this.minValue = minValue;
        if (currentValue < minValue) {
            updateCurrentValue(minValue);
        }

        setSliderRange();
    }

    public int getValue() {
        return this.currentValue;
    }

    public void setValue(int value) {
        if (value < this.minValue) {
            throw new IllegalArgumentException(
                    String.format(
                            "value (%d) should not be smaller than minValue (%d)",
                            value,
                            this.minValue));
        }
        if (value > this.maxValue) {
            throw new IllegalArgumentException(
                    String.format(
                            "value (%d) should not be bigger than maxValue (%d)",
                            value,
                            this.maxValue));
        }
        updateCurrentValue(value);
    }

    public void addOnChangeListener(MaterialIntSliderAndEditText.OnChangeListener listener) {
        this.changeListeners.add(listener);
    }

    public void removeOnChangeListener(MaterialIntSliderAndEditText.OnChangeListener listener) {
        this.changeListeners.remove(listener);
    }

    public void clearOnChangeListeners() {
        this.changeListeners.clear();
    }

    public interface OnChangeListener {
        void onValueChange(@NonNull MaterialIntSliderAndEditText intSliderAndEditText, int value);
    }
}
