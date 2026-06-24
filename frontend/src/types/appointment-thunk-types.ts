export interface IFetchAppointmentsResponse {
  data: {
    content: import("@/types/appointment-types").Appointment[];
    totalElements: number;
    totalPages: number;
    size: number;
    number: number;
  };
  message: string;
}

export interface ICreateAppointmentRequest {
  petId: number;
  specialistId: number;
  date: string;
  notes?: string;
  duration: number;
}

export interface ICreateAppointmentResponse {
  data: import("@/types/appointment-types").Appointment;
  message: string;
}

export interface IUpdateAppointmentRequest {
  appointmentId: number;
  date?: string;
  notes?: string;
  duration?: number;
  status?: "SCHEDULED" | "COMPLETED" | "CANCELLED";
}

export interface IUpdateAppointmentResponse {
  data: import("@/types/appointment-types").Appointment;
  message: string;
}

export interface ISubmitFeedbackRequest {
  appointmentId: number;
  feedback: string;
  rating: number;
}

export interface ISubmitFeedbackResponse {
  message: string;
}

export interface ICancelAppointmentRequest {
  appointmentId: number;
}

export interface ICancelAppointmentResponse {
  message: string;
}

export interface ICompleteAppointmentRequest {
  appointmentId: number;
}

export interface ICompleteAppointmentResponse {
  message: string;
}
