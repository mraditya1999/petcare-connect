import { z } from "zod";

export const loginFormSchema = z.object({
  email: z
    .string()
    .nonempty("Email is required")
    .email({ message: "Invalid email format" }),
  password: z
    .string()
    .nonempty("Password is required")
    .min(8, { message: "Password must be at least 8 characters" })
    .regex(
      /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]+$/,
      {
        message:
          "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character",
      },
    ),
});

export const registerFormSchema = z.object({
  firstName: z
    .string()
    .nonempty("First Name is required")
    .min(3, "First Name must be at least 3 characters")
    .max(50, "First Name must be at most 50 characters long"),
  lastName: z
    .string()
    .nonempty("Last Name is required")
    .min(3, "Last Name must be at least 3 characters")
    .max(50, "Last Name must be at most 50 characters long"),
  email: z
    .string()
    .nonempty("Email is required")
    .email({ message: "Invalid email format" }),
  password: z
    .string()
    .nonempty("Password is required")
    .min(8, { message: "Password must be at least 8 characters" })
    .regex(
      /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]+$/,
      {
        message:
          "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character",
      },
    ),
});

export const forgetPasswordFormSchema = z.object({
  email: z.string().nonempty("Email is required").email("Invalid email format"),
});

export const resetPasswordFormSchema = z.object({
  password: z
    .string()
    .nonempty("Password is required")
    .min(8, { message: "Password must be at least 8 characters" })
    .regex(
      /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]+$/,
      {
        message:
          "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character",
      },
    )
    .min(1, "Password is required"),
});
