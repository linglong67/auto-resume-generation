package controller;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import model.Career;
import model.Education;
import model.PersonInfo;
import view.ResumeView;

public class ResumeController {
	private static final int START_ROW_NUM = 0;
	private static final int PHOTO_WIDTH = (int) (35 * 2.83465);
	private static final int PHOTO_HEIGHT = (int) (45 * 2.83465);

	private ResumeView view;
	private XSSFWorkbook workbook;

	public ResumeController() {
		view = new ResumeView();
		workbook = new XSSFWorkbook();
	}

	public static void main(String[] args) {
		ResumeController controller = new ResumeController();
		controller.createResume();
	}

	private void createResume() {
		PersonInfo personInfo = view.inputPersonInfo();
		List<Education> educations = view.inputEducationList();
		List<Career> careers = view.inputCareerList();
		String selfIntroduction = view.inputSelfIntroduction();

		Sheet resumeSheet = workbook.createSheet("이력서");
		createResumeSheet(resumeSheet, personInfo, educations, careers);

		CellStyle cellStyle = workbook.createCellStyle();
		getWrapCellStyle(cellStyle);

		Sheet selfIntroductionSheet = workbook.createSheet("자기소개서");
		createSelfIntroductionSheet(selfIntroductionSheet, cellStyle, selfIntroduction);

		saveWorkbookToFile();
	}

	private void getWrapCellStyle(CellStyle cellStyle) {
		cellStyle.setWrapText(true);
	}

	private void createResumeSheet(Sheet sheet, PersonInfo personInfo, List<Education> educations, List<Career> careers) {
		createPersonInfo(sheet, personInfo);
		createEducations(sheet, educations);
		createCareers(sheet, careers);
	}

	private void createPersonInfo(Sheet sheet, PersonInfo personInfo) {
		Row headerRow = sheet.createRow(START_ROW_NUM);
		headerRow.createCell(0).setCellValue("사진");
		headerRow.createCell(1).setCellValue("이름");
		headerRow.createCell(2).setCellValue("이메일");
		headerRow.createCell(3).setCellValue("주소");
		headerRow.createCell(4).setCellValue("전화번호");
		headerRow.createCell(5).setCellValue("생년월일");

		Row row = sheet.createRow(START_ROW_NUM + 1);
		createPhotoCell(sheet, row, personInfo.getPhoto());
		row.createCell(1).setCellValue(personInfo.getName());
		row.createCell(2).setCellValue(personInfo.getEmail());
		row.createCell(3).setCellValue(personInfo.getAddress());
		row.createCell(4).setCellValue(personInfo.getPhoneNumber());
		row.createCell(5).setCellValue(personInfo.getBirthDate());
	}

	private void createPhotoCell(Sheet sheet, Row row, String photo) {
		try (InputStream photoStream = new FileInputStream(photo)) {
			BufferedImage originalImage = ImageIO.read(photoStream);

			byte[] imageBytes = getImageBytes(originalImage);
			int imageIndex = workbook.addPicture(imageBytes, Workbook.PICTURE_TYPE_PNG);

			// Drawing 객체를 생성하고 이미지 삽입
			XSSFDrawing drawing = (XSSFDrawing) sheet.createDrawingPatriarch();
			XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 0, 0, 0, 1, 1, 2);
			drawing.createPicture(anchor, imageIndex);

			// 이미지가 삽입된 행의 높이와 열의 너비를 조정
			// 96은 화면의 DPI(Dots Per Inch, 인치당 도트 수)
			// Excel에서 셀의 높이는 포인트(point) 단위로 표시(1 포인트는 1/72 인치)
			row.setHeightInPoints(PHOTO_HEIGHT * 72 / 96); // 픽셀을 point로 변경

			// 8이란 값은, 엑셀에서 사용되는 기본 문자 폭의 값
			// 엑셀에서는 한 개의 문자가 차지하는 너비를 1/256 단위로 계산
			int columnWidth = (int) Math.floor(((float) PHOTO_WIDTH / (float) 8) * 256);
			sheet.setColumnWidth(0, columnWidth);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private byte[] getImageBytes(BufferedImage originalImage) throws IOException {
		Image resizedImage = originalImage.getScaledInstance(PHOTO_WIDTH, PHOTO_HEIGHT, Image.SCALE_SMOOTH);
		BufferedImage resizedBufferedImage = new BufferedImage(PHOTO_WIDTH, PHOTO_HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2d = resizedBufferedImage.createGraphics();
		g2d.drawImage(resizedImage, 0, 0, null);
		g2d.dispose();

		// 조절된 이미지를 바이트 배열로 변환
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(resizedBufferedImage, "png", baos);
		return baos.toByteArray();
	}

	private void createEducations(Sheet sheet, List<Education> educations) {
		int rowIndex = sheet.getLastRowNum() + 1;

		Row headerRow = sheet.createRow(rowIndex++);
		headerRow.createCell(0).setCellValue("졸업년도");
		headerRow.createCell(1).setCellValue("학교명");
		headerRow.createCell(2).setCellValue("전공");
		headerRow.createCell(3).setCellValue("졸업여부");

		for (Education education : educations) {
			Row row = sheet.createRow(rowIndex++);
			row.createCell(0).setCellValue(education.getGraduationYear());
			row.createCell(1).setCellValue(education.getSchoolName());
			row.createCell(2).setCellValue(education.getMajor());
			row.createCell(3).setCellValue(education.getGraduationStatus());
		}
	}

	private void createCareers(Sheet sheet, List<Career> careers) {
		int rowIndex = sheet.getLastRowNum() + 1;

		Row headerRow = sheet.createRow(rowIndex++);
		headerRow.createCell(0).setCellValue("근무기간");
		headerRow.createCell(1).setCellValue("근무처");
		headerRow.createCell(2).setCellValue("담당업무");
		headerRow.createCell(3).setCellValue("근속연수");

		for (Career career : careers) {
			Row row = sheet.createRow(rowIndex++);
			row.createCell(0).setCellValue(career.getWorkPeriod());
			row.createCell(1).setCellValue(career.getCompanyName());
			row.createCell(2).setCellValue(career.getJobTitle());
			row.createCell(3).setCellValue(career.getEmploymentYears());
		}
	}

	private void createSelfIntroductionSheet(Sheet sheet, CellStyle cellStyle, String selfIntroduction) {
		Row row = sheet.createRow(START_ROW_NUM);
		Cell cell = row.createCell(0);

		cell.setCellValue(selfIntroduction);
		cell.setCellStyle(cellStyle);
	}

	private void saveWorkbookToFile() {
		try {
			// 엑셀 파일 저장
			String filename = "이력서.xlsx";

			FileOutputStream outputStream = new FileOutputStream(new File(filename));
			workbook.write(outputStream);
			workbook.close();

			System.out.println("이력서가 생성되었습니다.");

		} catch (IOException e) {
			System.out.println("엑셀 파일 저장 중 오류가 발생했습니다.");
			e.printStackTrace();
		}
	}
}
