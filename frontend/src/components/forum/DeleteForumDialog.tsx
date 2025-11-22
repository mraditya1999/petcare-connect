import {
  AlertDialog,
  AlertDialogContent,
  AlertDialogHeader,
  AlertDialogFooter,
  AlertDialogTitle,
  AlertDialogDescription,
  AlertDialogCancel,
  AlertDialogAction,
} from "@/components/ui/alert-dialog";

interface IDeleteForumDialogProps {
  open: boolean;
  onClose: (open: boolean) => void;
  onDelete: () => void;
}

export const DeleteForumDialog: React.FC<IDeleteForumDialogProps> = ({
  open,
  onClose,
  onDelete,
}) => (
  <AlertDialog open={open} onOpenChange={onClose}>
    <AlertDialogContent>
      <AlertDialogHeader>
        <AlertDialogTitle>Delete forum?</AlertDialogTitle>
        <AlertDialogDescription>
          Are you sure you want to delete this forum? This action cannot be
          undone.
        </AlertDialogDescription>
      </AlertDialogHeader>
      <AlertDialogFooter>
        <AlertDialogCancel>Cancel</AlertDialogCancel>
        <AlertDialogAction
          className="bg-destructive text-destructive-foreground hover:bg-destructive/90"
          onClick={onDelete}
        >
          Delete
        </AlertDialogAction>
      </AlertDialogFooter>
    </AlertDialogContent>
  </AlertDialog>
);
