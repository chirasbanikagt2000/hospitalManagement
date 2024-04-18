package HospitalManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class Hospital {
	private static final String url ="jdbc:mysql://localhost:3306/hospital";
	private static final String username ="root";
	private static final String password ="shubhadeep@7005";

	public static void main(String[] args) {
		try{
			Class.forName("com.mysql.cj.jdbc.Driver");
		}catch(ClassNotFoundException e){
			e.printStackTrace();
		}
		Scanner scanner = new Scanner(System.in);

		try{
			Connection connection = DriverManager.getConnection(url,username,password);
			Patient patient = new Patient(connection,scanner);
			Doctors doctor = new Doctors(connection);

			while(true){
				System.out.println("Hospital Management System");
				System.out.println("1. Add Patient");
				System.out.println("2. View Patients");
				System.out.println("3. View Doctors");
				System.out.println("4. Book Appointment");
				System.out.println("5. Exit");

				System.out.println("Enter your choice");

				int choice = scanner.nextInt();
				switch(choice){
					case 1 :
						//Add patient
						patient.addPatient();
						System.out.println();
						break;

					case 2 :
						//View Patient
						patient.viewPatients();
						System.out.println();
						break;
					case 3:
						//View Doctors
						doctor.viewDoctors();
						System.out.println();
						break;
					case 4:
						//Book Appointment
						bookappointment(patient,doctor,connection,scanner);
						System.out.println();
						break;

					case 5:
						return;
					default:
						System.out.println("Enter a valid choice");


				}
			}
		} catch (SQLException e){
			e.printStackTrace();
		}




	}

	public static void bookappointment(Patient patient, Doctors doctor, Connection connection, Scanner scanner){
		System.out.println("Enter patient id");
		int patient_Id = scanner.nextInt();
		System.out.println("Enter doctor id");
		int doctor_id = scanner.nextInt();
		System.out.println("Enter appointment date (YYYY-MM-DD");
		String appoinment_date = scanner.next();

		if(patient.getPatientById(patient_Id) && doctor.getDoctorById(doctor_id)){
			if(checkDoctorAvailability(doctor_id,appoinment_date, connection)){
				String appointmentQuery = "INSERT INTO appointments(patient_id,doctor_id,appointment_date) VALUES(?,?,?)";
				try{
					PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
					preparedStatement.setInt(1,patient_Id);
					preparedStatement.setInt(2,doctor_id);
					preparedStatement.setString(3,appoinment_date);
					int rows_affected = preparedStatement.executeUpdate();
					if(rows_affected>0){
						System.out.println("Appointment booked");
					}
					else{
						System.out.println("Failed to book");
					}

				}catch (SQLException e){
					e.printStackTrace();
				}

			}else{
				System.out.println("Doctor not available");
			}

		}
		else{
			System.out.println("Either doctor or patient doesnot exist");
		}



	}

	public static boolean checkDoctorAvailability(int doctorId, String appointmentDate, Connection connection){
		String query = "SELECT COUNT(*) from appointments where doctor_id = ? AND appointment_date=?";
		try{
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1,doctorId);
			preparedStatement.setString(2,appointmentDate);
			ResultSet resultSet = preparedStatement.executeQuery();
			if(resultSet.next()){
				int count = resultSet.getInt(1);
				if(count == 0){
					return true;
				}
				else{
					return false;
				}
			}

		}catch(SQLException e){
			e.printStackTrace();
		}
		return false;

	}


}
