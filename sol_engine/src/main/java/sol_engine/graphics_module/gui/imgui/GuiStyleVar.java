package sol_engine.graphics_module.gui.imgui;

import imgui.StyleVar;

public enum GuiStyleVar implements GuiFlags {
    /**
     * float
     */
    Alpha(StyleVar.Alpha),
    /**
     * vec2
     */
    WindowPadding(StyleVar.WindowPadding),
    /**
     * float
     */
    WindowRounding(StyleVar.WindowRounding),
    /**
     * float
     */
    WindowBorderSize(StyleVar.WindowBorderSize),
    /**
     * vec2
     */
    WindowMinSize(StyleVar.WindowMinSize),
    /**
     * Vec2
     */
    WindowTitleAlign(StyleVar.WindowTitleAlign),
    /**
     * float
     */
    ChildRounding(StyleVar.ChildRounding),
    /**
     * float
     */
    ChildBorderSize(StyleVar.ChildBorderSize),
    /**
     * float
     */
    PopupRounding(StyleVar.PopupRounding),
    /**
     * float
     */
    PopupBorderSize(StyleVar.PopupBorderSize),
    /**
     * vec2
     */
    FramePadding(StyleVar.FramePadding),
    /**
     * float
     */
    FrameRounding(StyleVar.FrameRounding),
    /**
     * float
     */
    FrameBorderSize(StyleVar.FrameBorderSize),
    /**
     * vec2
     */
    ItemSpacing(StyleVar.ItemSpacing),
    /**
     * vec2
     */
    ItemInnerSpacing(StyleVar.ItemInnerSpacing),
    /**
     * float
     */
    IndentSpacing(StyleVar.IndentSpacing),
    /**
     * Float
     */
    ScrollbarSize(StyleVar.ScrollbarSize),
    /**
     * Float
     */
    ScrollbarRounding(StyleVar.ScrollbarRounding),
    /**
     * float
     */
    GrabMinSize(StyleVar.GrabMinSize),
    /**
     * float
     */
    GrabRounding(StyleVar.GrabRounding),
    /**
     * float
     */
    TabRounding(StyleVar.TabRounding),
    /**
     * vec2
     */
    ButtonTextAlign(StyleVar.ButtonTextAlign),
    /**
     * vec2
     */
    SelectableTextAlign(StyleVar.SelectableTextAlign);

    private StyleVar nativeRep;

    GuiStyleVar(StyleVar nativeRep) {
        this.nativeRep = nativeRep;
    }

    StyleVar getNative() {
        return nativeRep;
    }

    @Override
    public int getI() {
        return 0;
    }
}
