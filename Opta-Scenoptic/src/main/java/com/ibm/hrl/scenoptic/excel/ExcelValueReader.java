package com.ibm.hrl.scenoptic.excel;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class ExcelValueReader {
	protected String fileName;
	protected Workbook wb;
	protected FormulaEvaluator formulaEvaluator;

	public ExcelValueReader(String fileName) throws IOException {
		this.fileName = fileName;
		try (InputStream inp = new FileInputStream(fileName)) {
			this.wb = WorkbookFactory.create(inp);
			this.formulaEvaluator = this.wb.getCreationHelper().createFormulaEvaluator();
		}
	}

	private Object cellValue(Cell cell, CellType cellType) {
		switch (cellType) {
			case BLANK:
				return null;
			case FORMULA:
				return cellValue(cell, formulaEvaluator.evaluateFormulaCell(cell));
			case BOOLEAN:
				return cell.getBooleanCellValue();
			case STRING:
				return cell.getStringCellValue();
			case NUMERIC:
				double value = cell.getNumericCellValue();
				if ((value == Math.rint(value)) && !Double.isInfinite(value)) {
					return (int) value;
				}
				return value;
			default:
				return null;
		}
	}

	public Object readCell(String sheet, int row, int col) {
		Sheet xSheet = this.wb.getSheet(sheet);
		if (null != xSheet) {
			Row xRow = xSheet.getRow(row - 1);
			if (null != xRow) {
				Cell cell = xRow.getCell(col - 1);

				if (cell != null) {
					CellType cellType = cell.getCellType();
					return cellValue(cell, cellType);
				}
			}
		}
		return null;
	}

	public void close() {
		try {
			this.wb.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
