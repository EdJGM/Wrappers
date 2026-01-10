import { Button, ButtonProps } from 'primereact/button';

import { COLORS } from '~/constants/colors';

interface ButtonCancelProps extends ButtonProps {
    icon?: React.ReactNode;
    className?: string;
    style?: React.CSSProperties;
}

export default function ButtonCancel({ icon, className, style, ...props }: ButtonCancelProps) {
    return (
        <Button
            {...props}
            icon={icon}
            className={`font-bold shadow-lg hover:shadow-xl transform hover:scale-105 transition-all duration-200 ${className || ''}`}
            style={{
                backgroundColor: COLORS.DANGER,
                borderColor: COLORS.DANGER,
                color: 'white',
                padding: '0.75rem 2rem',
                borderRadius: '8px',
                fontSize: '1rem',
                ...style
            }}
        />
    );
}