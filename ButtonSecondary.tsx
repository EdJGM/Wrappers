import { Button, ButtonProps } from 'primereact/button';

import { COLORS } from '~/constants/colors';

interface ButtonSecondaryProps extends ButtonProps {
    icon?: React.ReactNode;
    className?: string;
    style?: React.CSSProperties;
}

export default function ButtonPrimary({ icon, className, style, ...props }: ButtonSecondaryProps) {
    return (
        <Button
            {...props}
            icon={icon}
            className={`font-bold shadow-lg hover:shadow-xl transform hover:scale-105 transition-all duration-200 ${className || ''}`}
            style={{
                height: '3.5rem',
                backgroundColor: COLORS.SECONDARY,
                borderColor: COLORS.SECONDARY,
                color: 'white',
                padding: '0.75rem 2rem',
                borderRadius: '8px',
                fontSize: '1rem',
                ...style
            }}
        />
    );
}