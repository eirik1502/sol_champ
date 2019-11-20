package sol_engine.utils;

public interface Function {
    interface NoArgReturn<RETURN> extends Function {
        RETURN invoke();
    }

    interface OneArgReturn<A, RETURN> extends Function {
        RETURN invoke(A arg1);
    }

    interface TwoArgReturn<A, B, RETURN> extends Function {
        RETURN invoke(A arg1, B arg2);
    }

    interface ThreeArgReturn<A, B, C, RETURN> extends Function {
        RETURN invoke(A arg1, B arg2, C arg3);
    }

    interface FourArgReturn<A, B, C, D, RETURN> extends Function {
        RETURN invoke(A arg1, B arg2, C arg3, D arg4);
    }

    interface FiveArgReturn<A, B, C, D, E, RETURN> extends Function {
        RETURN invoke(A arg1, B arg2, C arg3, D arg4, E arg5);
    }

    interface SixArgReturn<A, B, C, D, E, F, RETURN> extends Function {
        RETURN invoke(A arg1, B arg2, C arg3, D arg4, E arg5, F arg6);
    }

    interface NoArg extends Function {
        void invoke();
    }

    interface OneArg<A> extends Function {
        void invoke(A arg1);
    }

    interface TwoArg<A, B> extends Function {
        void invoke(A arg1, B arg2);
    }

    interface ThreeArg<A, B, C> extends Function {
        void invoke(A arg1, B arg2, C arg3);
    }

    interface FourArg<A, B, C, D> extends Function {
        void invoke(A arg1, B arg2, C arg3, D arg4);
    }

    interface FiveArg<A, B, C, D, E> extends Function {
        void invoke(A arg1, B arg2, C arg3, D arg4, E arg5);
    }

    interface SixArg<A, B, C, D, E, F> extends Function {
        void invoke(A arg1, B arg2, C arg3, D arg4, E arg5, F arg6);
    }
}
