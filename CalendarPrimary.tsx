import { Calendar, CalendarProps } from 'primereact/calendar';

import { COLORS } from '~/constants/colors';

interface CalendarPrimaryProps extends CalendarProps {
    className?: string;
    style?: React.CSSProperties;
}

export default function CalendarPrimary({ className, style, ...props }: CalendarPrimaryProps) {
    return (
        <Calendar
            {...props}
            className={`w-full ${className || ''}`}
            pt={{
                root: {
                    className: "w-full",
                    style: {
                        padding: '0.75rem',
                        backgroundColor: 'white',
                        border: '2px solid',
                        borderRadius: '0.5rem',
                        borderColor: COLORS.PRIMARY
                    }
                },
                panel: {
                    className: 'shadow-lg border-2',
                    style: {
                        borderColor: COLORS.PRIMARY,
                        position: 'absolute',
                    }
                },
                header: {
                    style: {
                        padding: '0.3rem',
                        fontSize: '1.2rem',
                        background: COLORS.PRIMARY_DARK,
                        color: 'white'
                    }
                }
            }}
            style={{ ...style }}
            locale="es"
            showButtonBar
        />
    );
}