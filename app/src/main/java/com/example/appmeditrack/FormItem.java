package com.example.appmeditrack;

public class FormItem {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_FIELD = 1;
    public static final int TYPE_BUTTON = 2;
    public static final int TYPE_DROPDOWN = 3;

    private int type;
    private String label;
    private String hint;
    private String inputType; // "text", "number", etc.
    private String[] options; // For dropdown

    public FormItem(int type, String label, String hint, String inputType) {
        this.type = type;
        this.label = label;
        this.hint = hint;
        this.inputType = inputType;
    }

    public FormItem(int type, String label, String[] options) {
        this.type = type;
        this.label = label;
        this.options = options;
    }

    public int getType() { return type; }

    public String getLabel() { return label; }

    public String getHint() { return hint; }

    public String getInputType() { return inputType; }

    public String[] getOptions() { return options; }
}
