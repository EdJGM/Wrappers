import { InputText, InputTextProps } from 'primereact/inputtext';

import { COLORS } from '~/constants/colors';

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
            placeholder={placeholder }
            style={{
                borderColor: COLORS.PRIMARY,
                padding: '0.8rem',
                borderRadius: '0.5rem',
                ...style
            }}
        />
    );
}