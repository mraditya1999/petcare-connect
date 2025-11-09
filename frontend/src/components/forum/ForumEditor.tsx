import ReactQuill from "react-quill";
import "react-quill/dist/quill.snow.css";

interface ForumEditorProps {
  value: string;
  onChange: (value: string) => void;
  placeholder?: string;
}

const ForumEditor: React.FC<ForumEditorProps> = ({
  value,
  onChange,
  placeholder = "Write your forum content here...",
}) => {
  return (
    <div className="min-h-[13rem]">
      <ReactQuill
        className="h-full"
        value={value}
        onChange={onChange}
        modules={{
          toolbar: [
            ["bold", "italic"],
            [{ list: "ordered" }, { list: "bullet" }],
            ["link"],
          ],
        }}
        formats={["bold", "italic", "list", "bullet", "link"]}
        placeholder={placeholder}
      />
    </div>
  );
};

export default ForumEditor;
