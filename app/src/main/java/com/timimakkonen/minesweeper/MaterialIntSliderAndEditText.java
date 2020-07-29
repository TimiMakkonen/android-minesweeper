package com.timimakkonen.minesweeper;

import android.content.Context;
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
 * TODO: document your custom view class.
 */
public class MaterialIntSliderAndEditText extends LinearLayout {

    private static final String TAG = "MatIntSliderAndEditT";

    private final List<OnChangeListener> changeListeners = new ArrayList<>();
    private Slider slider;
    private TextInputEditText editText;
    private int maxValue;
    private int minValue;
    private int currentValue;


    public MaterialIntSliderAndEditText(Context context) {
        super(context);
        init();
    }

    public MaterialIntSliderAndEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MaterialIntSliderAndEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {

        inflate(getContext(), R.layout.material_int_slider_and_edittext, this);

        slider = findViewById(R.id.slider_materialintsliderandedittext);
        editText = findViewById(R.id.edittext_materialintsliderandedittext);

        setUpListeners();
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
                setCurrentValue(valueToSet);
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
                        setCurrentValue(textValue);
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
                    setCurrentValue(textValue);
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
                    setCurrentValue(textValue);
                } catch (NumberFormatException ex) {
                    Log.d(TAG, "setUpListeners: editTextListener: Invalid integer");
                }
            }
            return false;
        });
    }

    private void setCurrentValue(int value) {
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
                            this.maxValue,
                            this.minValue));
        }

        this.maxValue = maxValue;
        if (currentValue > maxValue) {
            setCurrentValue(maxValue);
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
                            this.minValue,
                            this.maxValue));
        }

        this.minValue = minValue;
        if (currentValue < minValue) {
            setCurrentValue(minValue);
        }

        setSliderRange();
    }

    public int getValue() {
        return this.currentValue;
    }

    public void addOnChangeListener(MaterialIntSliderAndEditText.OnChangeListener listener) {
        this.changeListeners.add(listener);
    }

    public interface OnChangeListener {
        void onValueChange(@NonNull MaterialIntSliderAndEditText intSliderAndEditText, int value);
    }
}
