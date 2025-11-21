import React, { useEffect } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { useToast } from "@/components/ui/use-toast";
import { Label } from "@/components/ui/label";
import { updatePasswordSchema } from "@/utils/validations";
import { updatePassword } from "@/features/user/userThunk";
import { PasswordInput } from "../ui/PasswordInput";
import { useAppDispatch } from "@/app/hooks";
import { IUpdatePasswordRequest } from "@/types/profile-thunk-types";
import { handleError, showToast } from "@/utils/helpers";

const LoginAndSecurity: React.FC = () => {
  const dispatch = useAppDispatch();
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<IUpdatePasswordRequest>({
    resolver: zodResolver(updatePasswordSchema),
  });

  const { toast } = useToast();

  const onSubmit = async (data: IUpdatePasswordRequest) => {
    try {
      await dispatch(updatePassword(data)).unwrap();
      toast({
        title: "Success",
        description: "Password changed successfully",
      });
    } catch (error) {
      const errorMessage = handleError(error);
      showToast(errorMessage, "destructive");
    }
  };

  useEffect(() => {
    if (errors.currentPassword) {
      showToast(errors.currentPassword.message || "", "destructive");
    }
    if (errors.newPassword) {
      showToast(errors.newPassword.message || "", "destructive");
    }
    if (errors.confirmPassword) {
      showToast(errors.confirmPassword.message || "", "destructive");
    }
  }, [errors, toast]);

  return (
    <Card className="mx-auto mt-6 h-full">
      <CardHeader>
        <CardTitle className="text-lg">Change Password</CardTitle>
      </CardHeader>
      <CardContent>
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div>
            <Label>Current Password</Label>
            <PasswordInput
              {...register("currentPassword")}
              placeholder="Enter current password"
            />
          </div>
          <div>
            <Label>New Password</Label>
            <PasswordInput
              {...register("newPassword")}
              placeholder="Enter new password"
            />
          </div>
          <div>
            <Label>Re-enter New Password</Label>
            <PasswordInput
              {...register("confirmPassword")}
              placeholder="Re-enter new password"
            />
          </div>
          <Button type="submit" className="w-full">
            Change Password
          </Button>
        </form>
      </CardContent>
    </Card>
  );
};

export default LoginAndSecurity;
