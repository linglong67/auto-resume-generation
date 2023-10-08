package view;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import model.Career;
import model.Education;
import model.PersonInfo;

public class ResumeView {
	Scanner sc = new Scanner(System.in);

	public PersonInfo inputPersonInfo() {
		PersonInfo personInfo = new PersonInfo();

		System.out.print("사진 파일명을 입력하세요:");
		personInfo.setPhoto(sc.nextLine());

		System.out.print("이름을 입력하세요:");
		personInfo.setName(sc.nextLine());

		System.out.print("이메일을 입력하세요:");
		personInfo.setEmail(sc.nextLine());

		System.out.print("주소를 입력하세요:");
		personInfo.setAddress(sc.nextLine());

		System.out.print("전화번호를 입력하세요:");
		personInfo.setPhoneNumber(sc.nextLine());

		System.out.print("생년월일을 입력하세요 (예: 1990-01-01):");
		personInfo.setBirthDate(sc.nextLine());

		return personInfo;
	}

	public List<Career> inputCareerList() {
		List<Career> careerList = new ArrayList<>();

		while (true) {
			System.out.println("경력 정보를 입력하세요 (종료는 q):");
			System.out.println("근무기간 근무처 담당업무 근속연수");
			String careerInput = sc.nextLine();

			if ("q".equals(careerInput)) {
				break;
			}

			String[] sArr = careerInput.split(" ");
			careerList.add(new Career(sArr[0], sArr[1], sArr[2], sArr[3]));
		}

		return careerList;
	}

	public List<Education> inputEducationList() {
		List<Education> educationList = new ArrayList<>();

		while (true) {
			System.out.println("학력 정보를 입력하세요 (종료는 q):");
			System.out.println("졸업년도 학교명 전공 졸업여부");
			String educationInput = sc.nextLine();

			if ("q".equals(educationInput)) {
				break;
			}

			String[] sArr = educationInput.split(" ");
			educationList.add(new Education(sArr[0], sArr[1], sArr[2], sArr[3]));
		}

		return educationList;
	}

	public String inputSelfIntroduction() {
		StringBuilder sb = new StringBuilder();
		System.out.println("자기소개서를 입력하세요. 여러 줄을 입력하려면 빈 줄을 입력하세요.");

		while (true) {
			String line = sc.nextLine();
			if (line.isEmpty()) {
				break;
			} else {
				sb.append(line);
				sb.append("\n");
			}
		}

		return sb.toString();
	}
}
