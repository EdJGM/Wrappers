import { ProgressSpinner, ProgressSpinnerProps } from 'primereact/progressspinner';

export default function LoadingSpinner(props: ProgressSpinnerProps) {
    return (
        <ProgressSpinner
            style={{ width: '60px', height: '60px', ...props.style }}
            strokeWidth="4"
            fill="transparent"
            animationDuration="1s"
            {...props}
        />
    );
}