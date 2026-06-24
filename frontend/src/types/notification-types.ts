export interface NotificationItem {
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
  message: string;
  notificationType: string;
}

export interface IFetchNotificationsResponse {
  data: NotificationItem[];
  message: string;
}
