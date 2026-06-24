import axios from "axios";
import React, { useEffect, useMemo, useState } from "react";
import { useForm, Controller } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { useDispatch, useSelector } from "react-redux";
import { RootState, AppDispatch } from "@/app/store";
import { fetchPets } from "@/features/pet/petThunk";
import { fetchSpecialists } from "@/features/specialist/specialistThunk";
import { createAppointment } from "@/features/appointment/appointmentThunk";
import * as z from "zod";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { useToast } from "@/components/ui/use-toast";

const appointmentSchema = z.object({
  petId: z.string().nonempty({ message: "Pet is required" }),
  specialistId: z.string().nonempty({ message: "Specialist is required" }),
  date: z
    .string()
    .nonempty({ message: "Date is required" })
    .refine(
      (date) => {
        const selectedDate = new Date(date);
        const now = new Date();
        return selectedDate > now;
      },
      { message: "Appointment date must be in the future" },
    ),
  notes: z.string().optional(),
  duration: z
    .number()
    .min(1, { message: "Duration must be at least 1 minute" })
    .max(480, { message: "Duration cannot exceed 8 hours" }),
});

type AppointmentFormValues = z.infer<typeof appointmentSchema>;

const BookingForm: React.FC = () => {
  const dispatch = useDispatch<AppDispatch>();
  const { toast } = useToast();
  const { pets, loading: petsLoading } = useSelector(
    (state: RootState) => state.pet,
  );
  const { specialists, loading: specialistsLoading } = useSelector(
    (state: RootState) => state.specialist,
  );
  const [specialities, setSpecialities] = useState<string[]>([]);
  const [selectedSpeciality, setSelectedSpeciality] = useState<string>("");
  const { loading: appointmentLoading } = useSelector(
    (state: RootState) => state.appointment,
  );

  const {
    control,
    handleSubmit,
    formState: { errors },
    reset,
  } = useForm<AppointmentFormValues>({
    resolver: zodResolver(appointmentSchema),
  });

  useEffect(() => {
    dispatch(fetchPets());
    dispatch(fetchSpecialists({}));

    const fetchSpecialityOptions = async () => {
      try {
        const response = await axios.get<{ data: { name: string }[] }>(
          "/specialities",
        );
        const names = response.data?.data?.map((item) => item.name) || [];
        setSpecialities(names);
      } catch (error) {
        console.error("Failed to load specialities", error);
      }
    };

    fetchSpecialityOptions();
  }, [dispatch]);

  const filteredSpecialists = useMemo(() => {
    return selectedSpeciality
      ? specialists.filter(
          (specialist) =>
            (specialist.specialization || "").toLowerCase() ===
            selectedSpeciality.toLowerCase(),
        )
      : specialists;
  }, [specialists, selectedSpeciality]);

  const onSubmit = async (data: AppointmentFormValues) => {
    try {
      if (!pets.length) {
        throw new Error("No pet discovered. Please add one first.");
      }
      if (!specialists.length) {
        throw new Error(
          "No specialist discovered. Please ask admin to add specialists.",
        );
      }
      if (
        selectedSpeciality &&
        !filteredSpecialists.some((s) => s.id.toString() === data.specialistId)
      ) {
        throw new Error(
          "Selected specialist does not match selected speciality.",
        );
      }

      await dispatch(
        createAppointment({
          petId: parseInt(data.petId),
          specialistId: parseInt(data.specialistId),
          date: data.date,
          notes: data.notes,
          duration: data.duration,
        }),
      ).unwrap();
      toast({
        title: "Success",
        description: "Appointment booked successfully!",
      });
      reset();
    } catch (error: unknown) {
      let errorMessage = "Failed to book appointment";

      if (axios.isAxiosError(error)) {
        const status = error.response?.status;
        if (status === 400) {
          errorMessage =
            error.response?.data?.message || "Invalid appointment data";
        } else if (status === 404) {
          errorMessage = "Pet or specialist not found";
        } else if (status === 409) {
          errorMessage =
            "Appointment conflict - please choose a different time";
        } else if (status === 401) {
          errorMessage = "Please log in to book an appointment";
        } else if (status === 500) {
          errorMessage = "Server error - please try again later";
        } else if (error.message) {
          errorMessage = error.message;
        }
      } else if (error instanceof Error && error.message) {
        errorMessage = error.message;
      }

      toast({
        title: "Error",
        description: errorMessage,
        variant: "destructive",
      });
    }
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
      {pets.length === 0 && (
        <div className="rounded-md border border-yellow-300 bg-yellow-50 p-3 text-yellow-700">
          You have no registered pets yet. Please add a pet before booking an
          appointment.
        </div>
      )}
      {specialists.length === 0 && (
        <div className="rounded-md border border-yellow-300 bg-yellow-50 p-3 text-yellow-700">
          No specialists available yet. Please contact administrator to add
          specialists or try again later.
        </div>
      )}
      <div>
        <Label htmlFor="petId">Pet</Label>
        <Controller
          name="petId"
          control={control}
          render={({ field }) => (
            <Select
              onValueChange={field.onChange}
              defaultValue={field.value}
              disabled={petsLoading}
            >
              <SelectTrigger>
                <SelectValue
                  placeholder={petsLoading ? "Loading pets..." : "Select a pet"}
                />
              </SelectTrigger>
              <SelectContent>
                {pets.map((pet) => (
                  <SelectItem key={pet.petId} value={pet.petId.toString()}>
                    {pet.name} ({pet.species} - {pet.breed})
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          )}
        />
        {errors.petId && (
          <p className="text-sm text-red-500">{errors.petId.message}</p>
        )}
      </div>

      <div>
        <Label htmlFor="speciality">Speciality</Label>
        <select
          name="speciality"
          value={selectedSpeciality}
          onChange={(e) => setSelectedSpeciality(e.target.value)}
          className="w-full rounded-md border border-gray-300 bg-white px-3 py-2 text-sm text-gray-900 dark:bg-gray-800 dark:text-gray-100"
        >
          <option value="">Select a speciality</option>
          {specialities.map((item) => (
            <option key={item} value={item}>
              {item}
            </option>
          ))}
        </select>
      </div>

      <div>
        <Label htmlFor="specialistId">Specialist</Label>
        <Controller
          name="specialistId"
          control={control}
          render={({ field }) => (
            <Select
              onValueChange={field.onChange}
              defaultValue={field.value}
              disabled={
                specialistsLoading ||
                (!!selectedSpeciality && filteredSpecialists.length === 0)
              }
            >
              <SelectTrigger>
                <SelectValue
                  placeholder={
                    specialistsLoading
                      ? "Loading specialists..."
                      : selectedSpeciality && filteredSpecialists.length === 0
                        ? "No specialist for selected speciality"
                        : "Select a specialist"
                  }
                />
              </SelectTrigger>
              <SelectContent>
                {filteredSpecialists.map((specialist) => (
                  <SelectItem
                    key={specialist.id}
                    value={specialist.id.toString()}
                  >
                    {specialist.fullName} ({specialist.specialization})
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          )}
        />
        {errors.specialistId && (
          <p className="text-sm text-red-500">{errors.specialistId.message}</p>
        )}
        {selectedSpeciality && filteredSpecialists.length === 0 && (
          <p className="text-sm text-orange-600">
            No specialist found for selected speciality
          </p>
        )}
      </div>

      <div>
        <Label htmlFor="date">Date & Time</Label>
        <Controller
          name="date"
          control={control}
          render={({ field }) => <Input type="datetime-local" {...field} />}
        />
        {errors.date && (
          <p className="text-sm text-red-500">{errors.date.message}</p>
        )}
      </div>

      <div>
        <Label htmlFor="duration">Duration (in minutes)</Label>
        <Controller
          name="duration"
          control={control}
          render={({ field }) => (
            <Input
              type="number"
              {...field}
              onChange={(e) => field.onChange(parseInt(e.target.value))}
            />
          )}
        />
        {errors.duration && (
          <p className="text-sm text-red-500">{errors.duration.message}</p>
        )}
      </div>

      <div>
        <Label htmlFor="notes">Notes (Optional)</Label>
        <Controller
          name="notes"
          control={control}
          render={({ field }) => <Textarea {...field} />}
        />
      </div>

      <Button type="submit" disabled={appointmentLoading} className="w-full">
        {appointmentLoading ? "Booking..." : "Book Appointment"}
      </Button>
    </form>
  );
};

export default BookingForm;
