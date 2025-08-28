package com.ibm.hrl.scenoptic.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelValueWriter extends ExcelValueReader {
	public ExcelValueWriter(String fileName) throws IOException {
		super(fileName);
	}

	public void writeCell(String sheet, int row, int col, Object value) {
		Sheet xSheet = this.wb.getSheet(sheet);
		if (xSheet != null) {
			Row xRow = xSheet.getRow(row - 1);
			if (xRow != null) {
				Cell cell = xRow.getCell(col - 1);
				if (cell != null) {
					if (value == null)
						cell.setBlank();
					else if (value instanceof Boolean)
						cell.setCellValue((boolean) value);
					else if (value instanceof Number)
						cell.setCellValue((double) value);
					else if (value instanceof String)
						cell.setCellValue((String) value);
					else
						throw new RuntimeException("Can't write value: " + value + " to cell: " + sheet + "(" + row + "," + col + ")");
					wb.setForceFormulaRecalculation(true);
					return;
				}
			}
		}
		throw new RuntimeException("No such cell: " + sheet + "(" + row + "," + col + ")");
	}

	public void writeFile(String fileName, boolean overwrite) throws IOException {
		File file = new File(fileName);
		if (!overwrite && file.exists())
			throw new RuntimeException("File " + fileName + " already exists, not overwriting.");
		try (FileOutputStream out = new FileOutputStream(file)) {
			wb.write(out);
		}
	}
}
