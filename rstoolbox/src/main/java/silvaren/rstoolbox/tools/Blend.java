package silvaren.rstoolbox.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.ScriptIntrinsicBlend;

class Blend {

    interface BlendOp {
        void runOp(BaseSetup baseSetup);
    }

    private static BlendOp add = new BlendOp() {
        @Override
        public void runOp(BaseSetup baseSetup) {
            baseSetup.blendScript.forEachAdd(baseSetup.rsToolboxContext.ain, baseSetup.aout);
        }
    };

    private static BlendOp clear = new BlendOp() {
        @Override
        public void runOp(BaseSetup baseSetup) {
            baseSetup.blendScript.forEachClear(baseSetup.rsToolboxContext.ain, baseSetup.aout);
        }
    };

    private static BlendOp dst = new BlendOp() {
        @Override
        public void runOp(BaseSetup baseSetup) {
            baseSetup.blendScript.forEachDst(baseSetup.rsToolboxContext.ain, baseSetup.aout);
        }
    };

    private static BlendOp dstAtop = new BlendOp() {
        @Override
        public void runOp(BaseSetup baseSetup) {
            baseSetup.blendScript.forEachDstAtop(baseSetup.rsToolboxContext.ain, baseSetup.aout);
        }
    };

    private static BlendOp dstIn = new BlendOp() {
        @Override
        public void runOp(BaseSetup baseSetup) {
            baseSetup.blendScript.forEachDstIn(baseSetup.rsToolboxContext.ain, baseSetup.aout);
        }
    };

    private static BlendOp dstOut = new BlendOp() {
        @Override
        public void runOp(BaseSetup baseSetup) {
            baseSetup.blendScript.forEachDstOut(baseSetup.rsToolboxContext.ain, baseSetup.aout);
        }
    };

    private static BlendOp dstOver = new BlendOp() {
        @Override
        public void runOp(BaseSetup baseSetup) {
            baseSetup.blendScript.forEachDstOver(baseSetup.rsToolboxContext.ain, baseSetup.aout);
        }
    };

    private static BlendOp multiply = new BlendOp() {
        @Override
        public void runOp(BaseSetup baseSetup) {
            baseSetup.blendScript.forEachMultiply(baseSetup.rsToolboxContext.ain, baseSetup.aout);
        }
    };

    private static BlendOp src = new BlendOp() {
        @Override
        public void runOp(BaseSetup baseSetup) {
            baseSetup.blendScript.forEachSrc(baseSetup.rsToolboxContext.ain, baseSetup.aout);
        }
    };

    private static BlendOp srcAtop = new BlendOp() {
        @Override
        public void runOp(BaseSetup baseSetup) {
            baseSetup.blendScript.forEachSrcAtop(baseSetup.rsToolboxContext.ain, baseSetup.aout);
        }
    };

    private static BlendOp srcIn = new BlendOp() {
        @Override
        public void runOp(BaseSetup baseSetup) {
            baseSetup.blendScript.forEachSrcIn(baseSetup.rsToolboxContext.ain, baseSetup.aout);
        }
    };

    private static BlendOp srcOut = new BlendOp() {
        @Override
        public void runOp(BaseSetup baseSetup) {
            baseSetup.blendScript.forEachSrcOut(baseSetup.rsToolboxContext.ain, baseSetup.aout);
        }
    };

    private static BlendOp srcOver = new BlendOp() {
        @Override
        public void runOp(BaseSetup baseSetup) {
            baseSetup.blendScript.forEachSrcOver(baseSetup.rsToolboxContext.ain, baseSetup.aout);
        }
    };

    private static BlendOp subtract = new BlendOp() {
        @Override
        public void runOp(BaseSetup baseSetup) {
            baseSetup.blendScript.forEachSubtract(baseSetup.rsToolboxContext.ain, baseSetup.aout);
        }
    };

    private static BlendOp xor = new BlendOp() {
        @Override
        public void runOp(BaseSetup baseSetup) {
            baseSetup.blendScript.forEachXor(baseSetup.rsToolboxContext.ain, baseSetup.aout);
        }
    };

    private static void doOp(Context context, Bitmap srcBitmap, Bitmap dstBitmap, BlendOp blendOp) {
        BaseSetup baseSetup = BaseSetup.createFromBitmap(context, srcBitmap, dstBitmap);
        blendOp.runOp(baseSetup);
        baseSetup.aout.copyTo(dstBitmap);
    }

    private static void doOp(Context context, byte[] nv21ByteArraySrc, int width, int height,
                             byte[] nv21ByteArrayDst, BlendOp blendOp) {
        BaseSetup baseSetup = BaseSetup.createFromNv21Image(context, nv21ByteArraySrc, width, height,
                nv21ByteArrayDst);
        blendOp.runOp(baseSetup);
        baseSetup.aout.copyTo(nv21ByteArrayDst);
    }

    private static class BaseSetup {
        public final RSToolboxContext rsToolboxContext;
        public final Allocation aout;
        public final ScriptIntrinsicBlend blendScript;

        private BaseSetup(RSToolboxContext bitmapRSContext, Allocation aout, ScriptIntrinsicBlend scriptIntrinsicBlend) {
            this.rsToolboxContext = bitmapRSContext;
            this.aout = aout;
            this.blendScript = scriptIntrinsicBlend;
        }

        public static BaseSetup createFromBitmap(Context context, Bitmap srcBitmap, Bitmap dstBitmap) {
            RSToolboxContext bitmapRSContext = RSToolboxContext.createFromBitmap(context, srcBitmap);
            Allocation aout = Allocation.createFromBitmap(bitmapRSContext.rs, dstBitmap);

            ScriptIntrinsicBlend blendScript = ScriptIntrinsicBlend.create(
                    bitmapRSContext.rs, bitmapRSContext.ain.getElement());
            return new BaseSetup(bitmapRSContext, aout, blendScript);
        }

        public static BaseSetup createFromNv21Image(Context context, byte[] nv21ByteArraySrc,
                                                    int width, int height, byte[] nv21ByteArrayDst) {
            RSToolboxContext rsToolboxContext = RSToolboxContext.createFromNv21Image(context,
                    nv21ByteArraySrc, width, height);
            Allocation aout = Allocation.createTyped(rsToolboxContext.rs,
                    rsToolboxContext.ain.getType());

            ScriptIntrinsicBlend blendScript = ScriptIntrinsicBlend.create(
                    rsToolboxContext.rs, rsToolboxContext.ain.getElement());
            return new BaseSetup(rsToolboxContext, aout, blendScript);
        }
    }

    public static void add(Context context, Bitmap srcBitmap, Bitmap dstBitmap) {
        doOp(context, srcBitmap, dstBitmap, add);
    }

    public static void clear(Context context, Bitmap srcBitmap, Bitmap dstBitmap) {
        doOp(context, srcBitmap, dstBitmap, clear);
    }

    public static void dst(Context context, Bitmap srcBitmap, Bitmap dstBitmap) {
        doOp(context, srcBitmap, dstBitmap, dst);
    }

    public static void dstAtop(Context context, Bitmap srcBitmap, Bitmap dstBitmap) {
        doOp(context, srcBitmap, dstBitmap, dstAtop);
    }

    public static void dstIn(Context context, Bitmap srcBitmap, Bitmap dstBitmap) {
        doOp(context, srcBitmap, dstBitmap, dstIn);
    }

    public static void dstOut(Context context, Bitmap srcBitmap, Bitmap dstBitmap) {
        doOp(context, srcBitmap, dstBitmap, dstOut);
    }

    public static void dstOver(Context context, Bitmap srcBitmap, Bitmap dstBitmap) {
        doOp(context, srcBitmap, dstBitmap, dstOver);
    }

    public static void multiply(Context context, Bitmap srcBitmap, Bitmap dstBitmap) {
        doOp(context, srcBitmap, dstBitmap, multiply);
    }

    public static void src(Context context, Bitmap srcBitmap, Bitmap dstBitmap) {
        doOp(context, srcBitmap, dstBitmap, src);
    }

    public static void srcAtop(Context context, Bitmap srcBitmap, Bitmap dstBitmap) {
        doOp(context, srcBitmap, dstBitmap, srcAtop);
    }

    public static void srcIn(Context context, Bitmap srcBitmap, Bitmap dstBitmap) {
        doOp(context, srcBitmap, dstBitmap, srcIn);
    }

    public static void srcOut(Context context, Bitmap srcBitmap, Bitmap dstBitmap) {
        doOp(context, srcBitmap, dstBitmap, srcOut);
    }

    public static void srcOver(Context context, Bitmap srcBitmap, Bitmap dstBitmap) {
        doOp(context, srcBitmap, dstBitmap, srcOver);
    }

    public static void subtract(Context context, Bitmap srcBitmap, Bitmap dstBitmap) {
        doOp(context, srcBitmap, dstBitmap, subtract);
    }

    public static void xor(Context context, Bitmap srcBitmap, Bitmap dstBitmap) {
        doOp(context, srcBitmap, dstBitmap, xor);
    }
}
