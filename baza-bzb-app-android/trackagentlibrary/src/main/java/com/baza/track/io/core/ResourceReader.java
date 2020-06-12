package com.baza.track.io.core;

import android.content.Context;
import android.util.SparseArray;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;


public abstract class ResourceReader implements ResourceIds {

    public static class Ids extends ResourceReader {
        private volatile static Ids sInstance = null;

        public static Ids getInstance(Context context) {
            if (sInstance == null) {
                synchronized (Ids.class) {
                    if (sInstance == null && context != null) {
                        sInstance = new Ids(context.getPackageName(), context);
                    }
                }
            }
            return sInstance;
        }

        private Ids(String resourcePackageName, Context context) {
            super(context);
            mResourcePackageName = resourcePackageName;
            initialize();
        }

        @Override
        protected Class<?> getSystemClass() {
            return android.R.id.class;
        }

        @Override
        protected String getLocalClassName(Context context) {
            return mResourcePackageName + ".R$id";
        }

        private final String mResourcePackageName;
    }

    public static class Layouts extends ResourceReader {

        public Layouts(String resourcePackageName, Context context) {
            super(context);
            mResourcePackageName = resourcePackageName;
            initialize();
        }

        @Override
        protected Class<?> getSystemClass() {
            return android.R.layout.class;
        }

        @Override
        protected String getLocalClassName(Context context) {
            return mResourcePackageName + ".R$layout";
        }

        private final String mResourcePackageName;
    }

    @SuppressWarnings("unused")
    public static class Drawables extends ResourceReader {

        protected Drawables(String resourcePackageName, Context context) {
            super(context);
            mResourcePackageName = resourcePackageName;
            initialize();
        }

        @Override
        protected Class<?> getSystemClass() {
            return android.R.drawable.class;
        }

        @Override
        protected String getLocalClassName(Context context) {
            return mResourcePackageName + ".R$drawable";
        }

        private final String mResourcePackageName;
    }

    ResourceReader(Context context) {
        mContext = context;
        mIdNameToId = new HashMap<>();
        mIdToIdName = new SparseArray<>();
    }

    @Override
    public boolean knownIdName(String name) {
        return mIdNameToId.containsKey(name);
    }

    @Override
    public int idFromName(String name) {
        return mIdNameToId.get(name);
    }

    @Override
    public String nameForId(int id) {
        return mIdToIdName.get(id);
    }

    private static void readClassIds(Class<?> platformIdClass, String namespace, Map<String, Integer> namesToIds) {
        try {
            final Field[] fields = platformIdClass.getFields();
            Class fieldType;
            String name;
            int value;
            for (final Field field : fields) {
                final int modifiers = field.getModifiers();
                if (Modifier.isStatic(modifiers)) {
                    fieldType = field.getType();
                    if (fieldType == int.class) {
                        name = field.getName();
                        value = field.getInt(null);
                        namesToIds.put((namespace == null ? name : namespace + ":" + name), value);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    protected abstract Class<?> getSystemClass();

    protected abstract String getLocalClassName(Context context);

    void initialize() {
        mIdNameToId.clear();
        mIdToIdName.clear();

//        final Class<?> sysIdClass = getSystemClass();
//        readClassIds(sysIdClass, "android", mIdNameToId);

        final String localClassName = getLocalClassName(mContext);
        try {
            final Class<?> rIdClass = Class.forName(localClassName);
            readClassIds(rIdClass, null, mIdNameToId);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        for (Map.Entry<String, Integer> idMapping : mIdNameToId.entrySet()) {
            mIdToIdName.put(idMapping.getValue(), idMapping.getKey());
        }
    }

    private final Context mContext;
    private final Map<String, Integer> mIdNameToId;
    private final SparseArray<String> mIdToIdName;
}
