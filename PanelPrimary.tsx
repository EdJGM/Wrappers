import { Panel, PanelProps } from 'primereact/panel';

import { COLORS } from '~/constants/colors';

interface PanelPrimaryProps extends PanelProps {
    style?: React.CSSProperties;
}

export default function PanelPrimary({ style, ...props }: PanelPrimaryProps) {
    return (
        <Panel
            {...props}
            pt={{
                header: {
                    style: {
                        background: COLORS.PRIMARY,
                        color: 'white',
                        borderRadius: '8px 8px 0 0',
                        padding: '1rem',
                        fontSize: '1.1rem',
                        fontWeight: 'bold'
                    }
                },
                content: {
                    style: {
                        padding: '1.5rem',
                        backgroundColor: '#fafafa'
                    }
                }
            }}
            style={{ backgroundColor: 'white', border: `1px solid ${COLORS.PRIMARY_BORDER}`, borderRadius: '8px', ...style }}
        />
    );
}