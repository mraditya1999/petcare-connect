import { useEffect } from "react";
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
  useEffect(() => {
    const map: Record<string, string> = {
      bold: "Bold",
      italic: "Italic",
      underline: "Underline",
      strike: "Strikethrough",
      ordered: "Numbered list",
      bullet: "Bullet list",
      link: "Insert link",
      image: "Insert image",
      code: "Inline code",
      "code-block": "Code block",
      blockquote: "Quote",
      header: "Heading",
      clean: "Remove formatting",
      align: "Alignment",
    };

    document.querySelectorAll(".ql-toolbar button").forEach((btn: Element) => {
      const cls = btn.className.replace("ql-", "");
      if (map[cls]) btn.setAttribute("title", map[cls]);
    });
  }, []);

  return (
    <div className="dark:quill-dark min-h-[25rem]">
      <ReactQuill
        className="h-full [&_.ql-editor]:min-h-[25rem]"
        value={value}
        onChange={onChange}
        modules={{
          toolbar: [
            [{ header: [1, 2, 3, false] }],
            ["bold", "italic", "underline", "strike"],
            [{ list: "ordered" }, { list: "bullet" }],
            [{ align: [] }],
            ["blockquote", "code-block"],
            ["link", "image"],
            ["clean"],
          ],
        }}
        formats={[
          "header",
          "bold",
          "italic",
          "underline",
          "strike",
          "list",
          "bullet",
          "blockquote",
          "code-block",
          "align",
          "link",
          "image",
        ]}
        placeholder={placeholder}
      />
    </div>
  );
};

export default ForumEditor;
