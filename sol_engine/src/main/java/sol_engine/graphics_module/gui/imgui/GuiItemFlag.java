package sol_engine.graphics_module.gui.imgui;

import imgui.internal.ItemFlag;

public enum GuiItemFlag {
    NoTabStop(ItemFlag.NoTabStop),  // false
    /**
     * Button() will return true multiple times based on io.KeyRepeatDelay and io.KeyRepeatRate settings.
     */
    ButtonRepeat(ItemFlag.ButtonRepeat),  // false
    /**
     * [BETA] Disable interactions but doesn't affect visuals yet. See github.com/ocornut/imgui/issues/211
     */
    Disabled(ItemFlag.Disabled),  // false
    NoNav(ItemFlag.NoNav),  // false
    NoNavDefaultFocus(ItemFlag.NoNavDefaultFocus),  // false
    /**
     * MenuItem/Selectable() automatically closes current Popup window
     */
    SelectableDontClosePopup(ItemFlag.SelectableDontClosePopup),  // false
    /**
     * [BETA] Represent a mixed/indeterminate value, generally multi-selection where values differ. Currently only supported by Checkbox() (later should support all sorts of widgets)
     */
    MixedValue(ItemFlag.MixedValue),  // false

    Default_(ItemFlag.Default_);


    private ItemFlag nativeFlag;

    GuiItemFlag(ItemFlag nativeFlag) {
        this.nativeFlag = nativeFlag;
    }

    public ItemFlag getNativeFlag() {
        return nativeFlag;
    }
}
