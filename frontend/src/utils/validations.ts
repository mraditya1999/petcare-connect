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

export const resetPasswordFormSchema = z
  .object({
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
    confirmPassword: z
      .string()
      .nonempty("Confirm Password is required")
      .min(8, { message: "Confirm Password must be at least 8 characters" })
      .regex(
        /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]+$/,
        {
          message:
            "Confirm Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character",
        },
      ),
  })
  .refine((data) => data.password === data.confirmPassword, {
    message: "Passwords do not match",
    path: ["confirmPassword"],
  });

export const updatePasswordSchema = z
  .object({
    currentPassword: z.string().min(1, "Current password is required"),
    newPassword: z
      .string()
      .min(8, "Password must be at least 8 characters long")
      .regex(/[A-Z]/, "Must contain at least one uppercase letter")
      .regex(/[0-9]/, "Must contain at least one number"),
    confirmPassword: z.string(),
  })
  .refine((data) => data.newPassword === data.confirmPassword, {
    message: "Passwords do not match",
    path: ["confirmPassword"],
  });

export const updatePasswordSchemaGoogle = z
  .object({
    newPassword: z
      .string()
      .min(8, "Password must be at least 8 characters long")
      .regex(/[A-Z]/, "Must contain at least one uppercase letter")
      .regex(/[0-9]/, "Must contain at least one number"),
    confirmPassword: z.string(),
  })
  .refine((data) => data.newPassword === data.confirmPassword, {
    message: "Passwords do not match",
    path: ["confirmPassword"],
  });

export const profileFormSchema = z.object({
  firstName: z.string().nonempty("First name is required"),
  lastName: z.string().nonempty("Last name is required"),
  email: z
    .string()
    .email("Invalid email address")
    .nonempty("Please enter a valid email address"),

  mobileNumber: z
    .string()
    .nonempty("Mobile number is required")
    .refine(
      (val) => /^\d+$/.test(val),
      "Mobile number must contain only digits",
    )
    .refine(
      (val) => val.length === 10,
      "Mobile number must be exactly 10 digits",
    ),

  pincode: z
    .string()
    .nonempty("Pincode is required")
    .refine((val) => /^\d+$/.test(val), "Pincode must contain only digits")
    .refine((val) => val.length === 6, "Pincode must be exactly 6 digits"),

  city: z.string().optional(),
  state: z.string().optional(),
  country: z.string().optional(),
  locality: z.string().optional(),
});

export const createForumSchema = z.object({
  title: z
    .string()
    .nonempty("Forum title is required")
    .min(5, "Title must be at least 5 characters long")
    .max(100, "Title must be at most 100 characters long"),

  tags: z
    .array(z.string().nonempty("Tag cannot be empty"))
    .min(1, "At least one tag is required")
    .max(5, "You can add up to 5 tags"),

  content: z
    .string()
    .nonempty("Content is required")
    .min(100, "Content must be at least 100 characters long"),
});

export const updateForumSchema = z.object({
  title: z
    .string()
    .nonempty("Title is required")
    .min(5, "Title must be at least 5 characters long")
    .max(100, "Title must be at most 100 characters"),

  content: z
    .string()
    .nonempty("Content is required")
    .min(200, "Content must be at least 200 characters long")
    .max(5000, "Content must be at most 5000 characters"),

  tags: z
    .array(z.string().nonempty("Tag cannot be blank"))
    .min(1, "At least one tag is required")
    .max(5, "You can add up to 5 tags"),
});

export const createCommentSchema = z.object({
  forumId: z.string().nonempty("Forum ID is required"),
  userId: z.number().int().positive("User ID must be a positive integer"),
  text: z
    .string()
    .nonempty("Comment text is required")
    .min(5, "Comment must be at least 5 characters long")
    .max(1000, "Comment must be at most 1000 characters long"),
  parentId: z.string().optional(),
});

export const updateCommentSchema = createCommentSchema.pick({
  text: true,
});

export type UpdateCommentInput = z.infer<typeof updateCommentSchema>;
export type CreateCommentInput = z.infer<typeof createCommentSchema>;
export type UpdateForumInput = z.infer<typeof updateForumSchema>;
export type CreateForumInput = z.infer<typeof createForumSchema>;
