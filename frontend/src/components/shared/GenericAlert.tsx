import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert";
import { AlertCircle, ShieldAlert } from "lucide-react";

interface IGenericAlert {
  title: string;
  message: string;
  success: boolean;
}

const GenericAlert: React.FC<IGenericAlert> = ({ title, message, success }) => {
  return (
    <Alert variant={success ? "default" : "destructive"} className="w-auto">
      {success ? <ShieldAlert /> : <AlertCircle />}
      <AlertTitle>{title}</AlertTitle>
      <AlertDescription>{message}</AlertDescription>
    </Alert>
  );
};
export default GenericAlert;
