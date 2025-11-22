import { toast } from "@/components/ui/use-toast";
import { CheckCircle, AlertCircle } from "lucide-react";

type ToastType = "success" | "error";

interface ShowToastOptions {
  description: string;
  type?: ToastType;
  duration?: number;
}

const ShowToast = ({
  description,
  type = "success",
  duration = 3000,
}: ShowToastOptions) => {
  toast({
    duration,
    variant: type === "success" ? "default" : "destructive",
    description: (
      <div className="flex items-center gap-2">
        {type === "success" ? (
          <CheckCircle className="h-5 w-5 text-green-500" />
        ) : (
          <AlertCircle className="h-5 w-5 text-red-500" />
        )}
        <span>{description}</span>
      </div>
    ),
  });
};

export default ShowToast;
