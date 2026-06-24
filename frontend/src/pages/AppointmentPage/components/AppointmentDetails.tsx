import React, { useState } from "react";
import { useDispatch } from "react-redux";
import { AppDispatch } from "@/app/store";
import {
  cancelAppointment,
  completeAppointment,
  submitFeedback,
} from "@/features/appointment/appointmentThunk";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import FeedbackForm from "./FeedbackForm";
import { Appointment } from "@/types/appointment-types";
import { useToast } from "@/components/ui/use-toast";
interface AppointmentDetailsProps {
  appointment: Appointment;
}

const AppointmentDetails: React.FC<AppointmentDetailsProps> = ({
  appointment,
}) => {
  const dispatch = useDispatch<AppDispatch>();
  const { toast } = useToast();
  const [showFeedbackForm, setShowFeedbackForm] = useState(false);

  const handleCancel = async () => {
    try {
      await dispatch(
        cancelAppointment({ appointmentId: appointment.appointmentId }),
      ).unwrap();
      toast({
        title: "Success",
        description: "Appointment cancelled successfully",
      });
    } catch (error: unknown) {
      const message =
        error instanceof Error ? error.message : "Failed to cancel appointment";
      toast({
        title: "Error",
        description: message,
        variant: "destructive",
      });
    }
  };

  const handleComplete = async () => {
    try {
      await dispatch(
        completeAppointment({ appointmentId: appointment.appointmentId }),
      ).unwrap();
      toast({
        title: "Success",
        description: "Appointment completed successfully",
      });
    } catch (error: unknown) {
      const message =
        error instanceof Error
          ? error.message
          : "Failed to complete appointment";
      toast({
        title: "Error",
        description: message,
        variant: "destructive",
      });
    }
  };

  const handleFeedbackSubmit = async (values: {
    feedback: string;
    rating: number;
  }) => {
    try {
      await dispatch(
        submitFeedback({
          appointmentId: appointment.appointmentId,
          feedback: values.feedback,
          rating: values.rating,
        }),
      ).unwrap();
      toast({
        title: "Success",
        description: "Feedback submitted successfully",
      });
      setShowFeedbackForm(false);
    } catch (error: unknown) {
      const message =
        error instanceof Error ? error.message : "Failed to submit feedback";
      toast({
        title: "Error",
        description: message,
        variant: "destructive",
      });
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case "SCHEDULED":
        return "bg-blue-100 text-blue-800";
      case "COMPLETED":
        return "bg-green-100 text-green-800";
      case "CANCELLED":
        return "bg-red-100 text-red-800";
      default:
        return "bg-gray-100 text-gray-800";
    }
  };

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center justify-between">
          <span>Appointment #{appointment.appointmentId}</span>
          <Badge className={getStatusColor(appointment.status)}>
            {appointment.status}
          </Badge>
        </CardTitle>
      </CardHeader>
      <CardContent className="space-y-2">
        <p>
          <strong>Pet:</strong> {appointment.petName}
        </p>
        <p>
          <strong>Specialist:</strong> {appointment.specialistName}
        </p>
        <p>
          <strong>Date & Time:</strong>{" "}
          {new Date(appointment.date).toLocaleString()}
        </p>
        <p>
          <strong>Duration:</strong> {appointment.duration} minutes
        </p>
        {appointment.notes && (
          <p>
            <strong>Notes:</strong> {appointment.notes}
          </p>
        )}
        {appointment.feedback && (
          <p>
            <strong>Feedback:</strong> {appointment.feedback}
          </p>
        )}
        {appointment.rating && (
          <p>
            <strong>Rating:</strong> {appointment.rating} / 5 ⭐
          </p>
        )}

        <div className="mt-4 flex gap-2">
          {appointment.status === "SCHEDULED" && (
            <>
              <Button variant="outline" size="sm" onClick={handleCancel}>
                Cancel
              </Button>
              <Button variant="outline" size="sm" onClick={handleComplete}>
                Mark Complete
              </Button>
            </>
          )}
          {appointment.status === "COMPLETED" &&
            !appointment.feedback &&
            !showFeedbackForm && (
              <Button
                variant="outline"
                size="sm"
                onClick={() => setShowFeedbackForm(true)}
              >
                Leave Feedback
              </Button>
            )}
        </div>

        {showFeedbackForm && (
          <div className="mt-4">
            <FeedbackForm
              appointmentId={appointment.appointmentId}
              onSubmit={handleFeedbackSubmit}
            />
          </div>
        )}
      </CardContent>
    </Card>
  );
};

export default AppointmentDetails;
