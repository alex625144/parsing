CREATE TABLE lot_result
(
    id           UUID NOT NULL,
    dk           VARCHAR(255),
    url          VARCHAR(255),
    pdf_link     VARCHAR(255),
    status       INTEGER,
    price        DECIMAL,
    parsing_date date,
    CONSTRAINT pk_lot_result PRIMARY KEY (id)
);
CREATE TABLE lot_pdf_result
(
    id          UUID    NOT NULL,
    price       DECIMAL,
    amount      INTEGER NOT NULL,
    totalAmount INTEGER NOT NULL,
    model       VARCHAR(255),
    "Lot.id"    UUID,
    CONSTRAINT pk_lot_pdf_result PRIMARY KEY (id)
);

ALTER TABLE lot_pdf_result
    ADD CONSTRAINT "FK_LOT_PDF_RESULT_ON_LOT.ID" FOREIGN KEY ("Lot.id") REFERENCES lot_result (id);