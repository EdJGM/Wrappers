import { AutoComplete, AutoCompleteProps } from 'primereact/autocomplete';

import { COLORS } from '~/constants/colors';

interface AutoCompletePrimaryProps extends AutoCompleteProps {
    className?: string;
    style?: React.CSSProperties;
    inputClassName?: string;
}

export default function AutoCompletePrimary({
    className,
    style,
    inputClassName,
    ...props
}: AutoCompletePrimaryProps) {
    return (
        <AutoComplete
            {...props}
            className={className}
            inputClassName={`w-full !rounded-lg !border-2 !bg-white !px-3 !py-3 !text-gray-900 placeholder:!text-gray-400 focus:!outline-none focus:!ring-2 transition ${inputClassName || ''}`}
            inputStyle={{
                borderColor: COLORS.PRIMARY,
                color: COLORS.PRIMARY_DARK,
            }} 
            style={{ width: '100%', ...style }}
            minLength={props.minLength ?? 3}
            disabled={props.disabled}
            placeholder={props.placeholder || 'Escriba para buscar...'}
        />
    );
}