package com.example.ttc.makeyouknowapp.db;

/**
 * Created by ttc on 2017/3/14.
 */

public class ZhihuNewsDbSchema {
    public static final class CacheNewsTable{
        public static final String NAME = "cacheNews";

        public static final class Cols{
            public static final String BODY ="body";
            public static final String TITLE = "title";
            public static final String IMAGE = "image";
            public static final String SHAREURI = "shareUri";
            public static final String IMAGES = "images";
            public static final String ID = "id";
        }
    }
    public static final class CollectNewsTable{
        public static final String NAME = "colletTable";

        public static final class Cols{
            public static final String TITLE = "title";
            public static final String IMAGE = "image";
            public static final String SHAREURI = "shareUri";
            public static final String IMAGES = "images";
            public static final String ID = "id";
        }

    }
}
