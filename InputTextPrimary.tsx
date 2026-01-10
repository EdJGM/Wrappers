import { InputText, InputTextProps } from 'primereact/inputtext';

interface InputTextPrimaryProps extends InputTextProps {
    className?: string;
    style?: React.CSSProperties;
    placeholder?: string;
}

export default function InputTextPrimary({ className, style, placeholder, ...props }: InputTextPrimaryProps) {
    return (
        <InputText
            {...props}
            className={`bg-white border-2 border-gray-400 p-1 rounded ${className || ''}`}
            placeholder={placeholder || 'Filtrar...'}
            style={{ ...style }}
        />
    );
}