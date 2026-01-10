import { Checkbox, CheckboxProps } from 'primereact/checkbox';

import { COLORS } from '~/constants/colors';

interface CheckboxPrimaryProps extends CheckboxProps {
    style?: React.CSSProperties;
}

export default function CheckboxPrimary({ style, ...props }: CheckboxPrimaryProps) {
    return (
        <Checkbox
            {...props}
            pt={{
                box: {
                    style: {
                        border: `2px solid ${COLORS.PRIMARY}`,
                        borderRadius: '0.375rem'
                    }
                }
            }}
            style={{ ...style }}
        />
    );
}