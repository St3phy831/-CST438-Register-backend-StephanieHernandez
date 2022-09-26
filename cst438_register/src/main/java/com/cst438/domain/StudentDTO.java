package com.cst438.domain;

import java.util.Objects;

public class StudentDTO {
	public int student_id;
	public String name;
	public String email;
	public int statusCode;
	public String status;
	
	public StudentDTO() {
		this.student_id=0;
		this.email=null;
		this.name=null;
		this.statusCode = 0;
		this.status = null;
	}
	
	
	public StudentDTO(String email, String name) {
		this.student_id=0;
		this.email=email;
		this.name=name;
		this.statusCode = 0;
		this.status = null;
	}

	@Override
	public String toString() {
		return "StudentDTO [student_id=" + student_id + ", name=" + name + ", email=" + email + ", statusCode="
				+ statusCode + ", status=" + status + "]";
	}


	@Override
	public int hashCode() {
		return Objects.hash(email, name, status, statusCode, student_id);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StudentDTO other = (StudentDTO) obj;
		return Objects.equals(email, other.email) && Objects.equals(name, other.name)
				&& Objects.equals(status, other.status) && statusCode == other.statusCode
				&& student_id == other.student_id;
	}

}
