import { Dropdown, DropdownProps } from 'primereact/dropdown';

import { COLORS } from '~/constants/colors';

interface DropdownPrimaryProps extends Omit<DropdownProps, 'optionLabel'> {
    className?: string;
    style?: React.CSSProperties;
    optionLabel?: string | ((item: unknown) => string);
}

export default function DropdownPrimary({ className, style, optionLabel, options, ...props }: DropdownPrimaryProps) {
    const processedOptions = typeof optionLabel === 'function' && options
        ? options.map((item: unknown) => ({
            ...(item as Record<string, unknown>),
            __displayLabel: optionLabel(item)
        }))
        : options;

    const finalOptionLabel = typeof optionLabel === 'function' ? '__displayLabel' : optionLabel;

    return (
        <Dropdown
            {...props}
            options={processedOptions}
            optionLabel={finalOptionLabel}
            className={`w-full ${className || ''}`}
            pt={{
                root: {
                    style: {
                        borderColor: COLORS.PRIMARY,
                        borderWidth: '2px'
                    }
                },
                filterInput: {
                    style: {
                        border: `2px solid ${COLORS.PRIMARY_BORDER}`,
                    }
                }
            }}
            style={{ ...style }}
        />
    );
}