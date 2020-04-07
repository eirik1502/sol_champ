package sol_engine.graphics_module.gui.imgui;

import imgui.WindowFlag;

/**
 * Decriptions copied from kotlin-graphics:imgui
 */
public enum GuiWindowFlags implements GuiFlags {
    None(0),
    /**
     * Disable title-bar
     */
    NoTitleBar(WindowFlag.NoTitleBar.i),
    /**
     * Disable user resizing with the lower-right grip
     */
    NoResize(WindowFlag.NoResize.i),
    /**
     * Disable user moving the window
     */
    NoMove(WindowFlag.NoMove.i),
    /**
     * Disable scrollbars (window can still scroll with mouse or programmatically)
     */
    NoScrollbar(WindowFlag.NoScrollbar.i),
    /**
     * Disable user vertically scrolling with mouse wheel. On child window, mouse wheel will be forwarded to the parent
     * unless noScrollbar is also set.
     */
    NoScrollWithMouse(WindowFlag.NoScrollWithMouse.i),
    /**
     * Disable user collapsing window by double-clicking on it
     */
    NoCollapse(WindowFlag.NoCollapse.i),
    /**
     * Resize every window to its content every frame
     */
    AlwaysAutoResize(WindowFlag.AlwaysAutoResize.i),
    /**
     * Disable drawing background color (WindowBg, etc.) and outside border. Similar as using SetNextWindowBgAlpha(0.0f).(1 shl 7)
     */
    NoBackground(WindowFlag.NoBackground.i),
    /**
     * Never load/save settings in .ini file
     */
    NoSavedSettings(WindowFlag.NoSavedSettings.i),
    /**
     * Disable catching mouse or keyboard inputs
     */
    NoMouseInputs(WindowFlag.NoMouseInputs.i),
    /**
     * Has a menu-bar
     */
    MenuBar(WindowFlag.MenuBar.i),
    /**
     * Allow horizontal scrollbar to appear (off by default). You may use SetNextWindowContentSize(ImVec2(width),0.0f));
     * prior to calling Begin() to specify width. Read code in imgui_demo in the "Horizontal Scrolling" section.
     */
    HorizontalScrollbar(WindowFlag.HorizontalScrollbar.i),
    /**
     * Disable taking focus when transitioning from hidden to visible state
     */
    NoFocusOnAppearing(WindowFlag.NoFocusOnAppearing.i),
    /**
     * Disable bringing window to front when taking focus (e.g. clicking on it or programmatically giving it focus)
     */
    NoBringToFrontOnFocus(WindowFlag.NoBringToFrontOnFocus.i),
    /**
     * Always show vertical scrollbar (even if ContentSize.y lessThan Size.y)
     */
    AlwaysVerticalScrollbar(WindowFlag.AlwaysVerticalScrollbar.i),
    /**
     * Always show horizontal scrollbar (even if ContentSize.x lessThan Size.x)
     */
    AlwaysHorizontalScrollbar(WindowFlag.AlwaysHorizontalScrollbar.i),
    /**
     * Ensure child windows without border uses style.WindowPadding (ignored by default for non-bordered child windows),
     * because more convenient)
     */
    AlwaysUseWindowPadding(WindowFlag.AlwaysUseWindowPadding.i),
    /**
     * No gamepad/keyboard navigation within the window
     */
    NoNavInputs(WindowFlag.NoNavInputs.i),
    /**
     * No focusing toward this window with gamepad/keyboard navigation (e.g. skipped by CTRL+TAB)
     */
    NoNavFocus(WindowFlag.NoNavFocus.i),
    /**
     * Append '*' to title without affecting the ID, as a convenience to avoid using the ### operator.
     * When used in a tab/docking context, tab is selected on closure and closure is deferred by one frame
     * to allow code to cancel the closure (with a confirmation popup, etc.) without flicker.
     */
    UnsavedDocument(WindowFlag.UnsavedDocument.i),

    NoNav(NoNavInputs.value | NoNavFocus.value),

    NoDecoration(NoTitleBar.value | NoResize.value | NoCollapse.value),

    NoInputs(NoMouseInputs.value | NoNavInputs.value | NoNavFocus.value);


    private int value;

    GuiWindowFlags(int value) {
        this.value = value;
    }

    @Override
    public int getI() {
        return value;
    }
}
