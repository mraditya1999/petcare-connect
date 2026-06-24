import React from "react";
import { useForm, Controller } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { Button } from "@/components/ui/button";
import { Textarea } from "@/components/ui/textarea";
import { Label } from "@/components/ui/label";
import { Input } from "@/components/ui/input";

const feedbackSchema = z.object({
  feedback: z.string().nonempty({ message: "Feedback is required" }),
  rating: z.number().min(1).max(5),
});

type FeedbackFormValues = z.infer<typeof feedbackSchema>;

interface FeedbackFormProps {
  appointmentId: number;
  onSubmit: (values: FeedbackFormValues) => void;
}

const FeedbackForm: React.FC<FeedbackFormProps> = ({ onSubmit }) => {
  const {
    control,
    handleSubmit,
    formState: { errors },
  } = useForm<FeedbackFormValues>({
    resolver: zodResolver(feedbackSchema),
  });

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
      <div>
        <Label htmlFor="feedback">Feedback</Label>
        <Controller
          name="feedback"
          control={control}
          render={({ field }) => <Textarea {...field} />}
        />
        {errors.feedback && (
          <p className="text-sm text-red-500">{errors.feedback.message}</p>
        )}
      </div>

      <div>
        <Label htmlFor="rating">Rating (1-5)</Label>
        <Controller
          name="rating"
          control={control}
          render={({ field }) => (
            <Input
              type="number"
              min="1"
              max="5"
              {...field}
              onChange={(e) => field.onChange(parseInt(e.target.value))}
            />
          )}
        />
        {errors.rating && (
          <p className="text-sm text-red-500">{errors.rating.message}</p>
        )}
      </div>

      <Button type="submit">Submit Feedback</Button>
    </form>
  );
};

export default FeedbackForm;
