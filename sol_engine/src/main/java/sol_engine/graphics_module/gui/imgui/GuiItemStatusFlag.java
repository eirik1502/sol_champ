package sol_engine.graphics_module.gui.imgui;

import imgui.internal.ItemStatusFlag;

public enum GuiItemStatusFlag implements GuiFlags {
    None(ItemStatusFlag.None),
    HoveredRect(ItemStatusFlag.HoveredRect),
    HasDisplayRect(ItemStatusFlag.HasDisplayRect),
    /**
     * Value exposed by item was edited in the current frame (should match the bool return value of most widgets)
     */
    Edited(ItemStatusFlag.Edited),
    /**
     * Set when Selectable(), TreeNode() reports toggling a selection. We can't report "Selected" because reporting
     * the change allows us to handle clipping with less issues.
     */
    ToggledSelection(ItemStatusFlag.ToggledSelection),
    /**
     * Set if the widget/group is able to provide data for the ImGuiItemStatusFlags_Deactivated flag.
     */
    HasDeactivated(ItemStatusFlag.HasDeactivated),
    /**
     * Only valid if ImGuiItemStatusFlags_HasDeactivated is set.
     */
    Deactivated(ItemStatusFlag.Deactivated),

    //  #ifdef IMGUI_ENABLE_TEST_ENGINE
//  [imgui-test only]
    Openable(ItemStatusFlag.Openable),
    Opened(ItemStatusFlag.Opened),
    Checkable(ItemStatusFlag.Checkable),
    Checked(ItemStatusFlag.Checked);


    private ItemStatusFlag nativeFlag;

    GuiItemStatusFlag(ItemStatusFlag nativeFlag) {
        this.nativeFlag = nativeFlag;
    }

    public ItemStatusFlag getNative() {
        return nativeFlag;
    }

    @Override
    public int getI() {
        return nativeFlag.i;
    }
}
