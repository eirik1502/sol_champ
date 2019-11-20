package sol_engine.graphics_module.imgui;

import imgui.Cond;

public enum GuiCond {
    None(Cond.None),
    /**
     * Set the variable
     */
    Always(Cond.Always),
    /**
     * Set the variable once per runtime session (only the first call with succeed)
     */
    Once(Cond.Once),
    /**
     * Set the variable if the object/window has no persistently saved data (no entry in .ini file)
     */
    FirstUseEver(Cond.FirstUseEver),
    /**
     * Set the variable if the object/window is appearing after being hidden/inactive (or the first time)
     */
    Appearing(Cond.Appearing);

    private Cond nativeCond;

    GuiCond(Cond nativeCond) {
        this.nativeCond = nativeCond;
    }

    Cond getNativeCond() {
        return nativeCond;
    }
}
