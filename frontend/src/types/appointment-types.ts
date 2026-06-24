export interface Appointment {
  appointmentId: number;
  petOwnerId: number;
  petId: number;
  specialistId: number;
  date: string;
  notes: string;
  duration: number;
  status: "SCHEDULED" | "COMPLETED" | "CANCELLED";
  petOwnerName: string;
  specialistName: string;
  petName: string;
  feedback?: string;
  rating?: number;
}

export interface AppointmentRequest {
  petId: number;
  specialistId: number;
  date: string;
  notes?: string;
  duration: number;
}

export interface AppointmentUpdateRequest {
  date?: string;
  notes?: string;
  duration?: number;
  status?: "SCHEDULED" | "COMPLETED" | "CANCELLED";
}

export interface FeedbackRequest {
  feedback: string;
  rating: number;
}

export interface PaginatedAppointments {
  content: Appointment[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}
