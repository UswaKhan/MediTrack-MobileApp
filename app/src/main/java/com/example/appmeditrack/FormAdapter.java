package com.example.appmeditrack;

import android.app.DatePickerDialog;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<FormItem> items;
    private final RecyclerView recyclerView;
    private OnButtonClickListener onButtonClickListener;

    // For medicine suggestions and price auto-fill
    private List<String> medicineNames;
    private Map<String, Double> medicinePriceMap;

    // Store all field values here
    private final Map<String, String> fieldValues = new HashMap<>();

    public FormAdapter(List<FormItem> items, RecyclerView recyclerView) {
        this(items, recyclerView, null, null);
    }

    public FormAdapter(List<FormItem> items, RecyclerView recyclerView, List<String> medicineNames, Map<String, Double> medicinePriceMap) {
        this.items = items;
        this.recyclerView = recyclerView;
        this.medicineNames = medicineNames;
        this.medicinePriceMap = medicinePriceMap;
    }

    public interface OnButtonClickListener {
        void onClick();
    }

    public void setOnButtonClickListener(OnButtonClickListener listener) {
        this.onButtonClickListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case FormItem.TYPE_HEADER:
                return new HeaderViewHolder(inflater.inflate(R.layout.form_item_header, parent, false));
            case FormItem.TYPE_BUTTON:
                return new ButtonViewHolder(inflater.inflate(R.layout.form_item_button, parent, false));
            case FormItem.TYPE_DROPDOWN:
                return new DropdownViewHolder(inflater.inflate(R.layout.form_item_dropdown, parent, false));
            default:
                return new FieldViewHolder(inflater.inflate(R.layout.form_item_field, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        FormItem item = items.get(position);

        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).title.setText(item.getLabel());
        } else if (holder instanceof FieldViewHolder) {
            FieldViewHolder vh = (FieldViewHolder) holder;
            vh.label.setText(item.getLabel());
            vh.input.setHint(item.getHint());

            // Set the value if it exists in the map
            if (fieldValues.containsKey(item.getLabel())) {
                vh.input.setText(fieldValues.get(item.getLabel()));
            } else {
                vh.input.setText("");
            }

            // Remove previous TextWatcher if any
            if (vh.input.getTag() instanceof TextWatcher) {
                vh.input.removeTextChangedListener((TextWatcher) vh.input.getTag());
            }

            // Add TextWatcher to update fieldValues map
            TextWatcher watcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    fieldValues.put(item.getLabel(), s.toString());
                }
                @Override
                public void afterTextChanged(Editable s) {}
            };
            vh.input.addTextChangedListener(watcher);
            vh.input.setTag(watcher);

            // Medicine Name suggestions and price auto-fill
            if ("Medicine Name".equals(item.getLabel()) && medicineNames != null && medicinePriceMap != null) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(vh.input.getContext(), android.R.layout.simple_dropdown_item_1line, medicineNames);
                vh.input.setAdapter(adapter);

                vh.input.setOnItemClickListener((parent, view, pos, id) -> {
                    String selectedMedicine = parent.getItemAtPosition(pos).toString();
                    Double price = medicinePriceMap.get(selectedMedicine);
                    // Find the price field and set its value
                    for (int i = 0; i < items.size(); i++) {
                        if (items.get(i).getLabel().equals("Price (PKR)")) {
                            RecyclerView.ViewHolder priceHolder = recyclerView.findViewHolderForAdapterPosition(i);
                            if (priceHolder instanceof FieldViewHolder) {
                                ((FieldViewHolder) priceHolder).input.setText(price != null ? String.valueOf(price) : "");
                                // Also update the fieldValues map for price
                                fieldValues.put("Price (PKR)", price != null ? String.valueOf(price) : "");
                            }
                        }
                    }
                });
            }

            switch (item.getInputType()) {
                case "number":
                    vh.input.setInputType(InputType.TYPE_CLASS_NUMBER);
                    break;
                case "decimal":
                    vh.input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    break;
                case "date":
                    vh.input.setFocusable(false);
                    vh.input.setInputType(InputType.TYPE_NULL);
                    vh.input.setOnClickListener(v -> {
                        final Calendar calendar = Calendar.getInstance();
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH);
                        int day = calendar.get(Calendar.DAY_OF_MONTH);

                        DatePickerDialog datePickerDialog = new DatePickerDialog(
                                v.getContext(),
                                (view, selectedYear, selectedMonth, selectedDay) -> {
                                    String date = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                                    vh.input.setText(date);
                                    fieldValues.put(item.getLabel(), date);
                                },
                                year, month, day
                        );
                        datePickerDialog.show();
                    });
                    break;
                default:
                    vh.input.setInputType(InputType.TYPE_CLASS_TEXT);
            }
        } else if (holder instanceof ButtonViewHolder) {
            ((ButtonViewHolder) holder).button.setText(item.getLabel());
            ((ButtonViewHolder) holder).button.setOnClickListener(v -> {
                if (onButtonClickListener != null) onButtonClickListener.onClick();
            });
        } else if (holder instanceof DropdownViewHolder) {
            DropdownViewHolder dh = (DropdownViewHolder) holder;
            dh.label.setText(item.getLabel());
            ArrayAdapter<String> adapter = new ArrayAdapter<>(dh.spinner.getContext(), android.R.layout.simple_spinner_dropdown_item, item.getOptions());
            dh.spinner.setAdapter(adapter);
        }
    }

    // Now always get the value from the map, not from the ViewHolder
    public String getValueForLabel(String label) {
        return fieldValues.getOrDefault(label, "");
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
        }
    }

    static class FieldViewHolder extends RecyclerView.ViewHolder {
        TextView label;
        AutoCompleteTextView input;
        FieldViewHolder(@NonNull View itemView) {
            super(itemView);
            label = itemView.findViewById(R.id.label);
            input = itemView.findViewById(R.id.input);
        }
    }

    static class DropdownViewHolder extends RecyclerView.ViewHolder {
        TextView label;
        Spinner spinner;
        DropdownViewHolder(@NonNull View itemView) {
            super(itemView);
            label = itemView.findViewById(R.id.label);
            spinner = itemView.findViewById(R.id.dropdown);
        }
    }

    static class ButtonViewHolder extends RecyclerView.ViewHolder {
        Button button;
        ButtonViewHolder(@NonNull View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.buttonSubmit);
        }
    }
}