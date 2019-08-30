package sol_engine.utils;

public interface Function {
    interface OneArg<A, RETURN> extends Function {
        RETURN invoke(A arg1);
    }

    interface TwoArg<A, B, RETURN> extends Function {
        RETURN invoke(A arg1, B arg2);
    }

    interface ThreeArg<A, B, C, RETURN> extends Function {
        RETURN invoke(A arg1, B arg2, C arg3);
    }

    interface FourArg<A, B, C, D, RETURN> extends Function {
        RETURN invoke(A arg1, B arg2, C arg3, D arg4);
    }

    interface FiveArg<A, B, C, D, E, RETURN> extends Function {
        RETURN invoke(A arg1, B arg2, C arg3, D arg4, E arg5);
    }

    interface SixArg<A, B, C, D, E, F, RETURN> extends Function {
        RETURN invoke(A arg1, B arg2, C arg3, D arg4, E arg5, F arg6);
    }
}
