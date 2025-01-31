import { Button, ButtonProps } from "../ui/button";

interface CustomButtonProps extends ButtonProps {
  buttonText: string;
}

const CustomButton: React.FC<CustomButtonProps> = (props) => {
  return (
    <Button {...props} className={`rounded-full ${props.className}`}>
      {props.buttonText}
    </Button>
  );
};

export default CustomButton;
