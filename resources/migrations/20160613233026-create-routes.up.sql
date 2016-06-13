CREATE TABLE ROUTES(
   ID            SERIAL        PRIMARY KEY     NOT NULL,
   NAME          VARCHAR(50)                   NOT NULL,
   LENGTH        NUMERIC(18,4)                 DEFAULT 0,
   TYPE          VARCHAR(50)                   DEFAULT '',
   CHECKPOINTS   VARCHAR(50)                   DEFAULT '',
   LEVEL         VARCHAR(50)                   DEFAULT '',
   ELEVATION     NUMERIC(18,4)                 DEFAULT 0
);
