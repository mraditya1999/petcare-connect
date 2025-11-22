import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { PasswordInput } from "../ui/PasswordInput";
import { useAppDispatch } from "@/app/hooks";
import { IUpdatePasswordRequest } from "@/types/profile-thunk-types";
import { updatePassword } from "@/features/user/userThunk";
import { updatePasswordSchema } from "@/utils/validations";
import ShowToast from "../shared/ShowToast";
import { Label } from "../ui/label";

const LoginAndSecurity: React.FC = () => {
  const dispatch = useAppDispatch();

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<IUpdatePasswordRequest>({
    resolver: zodResolver(updatePasswordSchema),
  });

  const fields = [
    { label: "Current Password", name: "currentPassword" },
    { label: "New Password", name: "newPassword" },
    { label: "Re-enter New Password", name: "confirmPassword" },
  ] as const;

  const onSubmit = async (data: IUpdatePasswordRequest) => {
    try {
      await dispatch(updatePassword(data)).unwrap();
    } catch (error: unknown) {
      // Handle unexpected API errors only
      ShowToast({ description: String(error), type: "error" });
    }
  };

  return (
    <Card className="mx-auto mt-6 h-full">
      <CardHeader>
        <CardTitle className="text-lg">Change Password</CardTitle>
      </CardHeader>
      <CardContent>
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          {fields.map(({ label, name }) => (
            <div key={name}>
              <Label>{label}</Label>
              <PasswordInput
                {...register(name)}
                placeholder={`Enter ${label.toLowerCase()}`}
              />
              {errors[name] && (
                <p className="mt-1 text-sm text-red-500">
                  {errors[name]?.message}
                </p>
              )}
            </div>
          ))}
          <Button type="submit" className="w-full">
            Change Password
          </Button>
        </form>
      </CardContent>
    </Card>
  );
};

export default LoginAndSecurity;
