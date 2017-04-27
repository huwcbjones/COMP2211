DROP TABLE IF EXISTS TotalCost;
CREATE TEMPORARY TABLE TotalCost (
  date    TIMESTAMP,
  year    INT,
  month   TINYINT,
  day     TINYINT,
  hour    TINYINT,
  cost    DECIMAL(10, 7),
  total   INT,
  gender  CHAR(6),
  age     CHAR(5),
  income  CHAR(6),
  context CHAR(12)
);
CREATE INDEX date_TotalCost_IND
  ON TotalCost (year, month, day, hour);
INSERT INTO TotalCost (date, year, month, day, hour, gender, age, income, context, cost, total)
  SELECT
    TO_TIMESTAMP(CONCAT(`i`.`YEAR`, '-', `i`.`MONTH`, '-', `i`.`DAY`, ' ', `i`.`HOUR`), 'YYYY-MM-DD HH24'),
    `i`.`YEAR`,
    `i`.`MONTH`,
    `i`.`DAY`,
    `i`.`HOUR`,
    `i`.`gender`,
    `i`.`age`,
    `i`.`income`,
    `i`.`context`,
    SUM(`i`.`cost`) + SUM(`c`.`click_cost`) AS totalCost,
    COUNT(*) as total
  FROM
    `Impressions` `i`
    RIGHT JOIN `Clicks` `c` ON `c`.ID = `i`.ID AND `i`.`date` BETWEEN DATEADD('MINUTE', -10, `c`.`date`) AND DATEADD('MINUTE', 10, `c`.`date`)
  GROUP BY `i`.`YEAR`, `i`.`MONTH`, `i`.`DAY`, `i`.`HOUR`, `i`.`gender`, `i`.`age`, `i`.`income`, `i`.`context`;