import { DataTable, DataTableProps, DataTableEditingRows, DataTableRowEditCompleteEvent, DataTableRowEditEvent, DataTableValueArray } from 'primereact/datatable';

import { COLORS } from '~/constants/colors';

interface DataTablePrimaryProps<T extends DataTableValueArray = DataTableValueArray> extends Omit<DataTableProps<T>, 'editMode' | 'editingRows' | 'onRowEditComplete' | 'onRowEditChange' | 'dataKey' | 'selectionMode' | 'cellSelection'> {
    className?: string;
    style?: React.CSSProperties;
    emptyMessageTitle?: string;
    emptyMessageSubtitle?: string;
    showPaginator?: boolean;
    defaultRows?: number;
    useCustomStyles?: boolean;

    editMode?: "row" | "cell";
    editingRows?: DataTableEditingRows;
    onRowEditComplete?: (event: DataTableRowEditCompleteEvent) => void;
    onRowEditChange?: (event: DataTableRowEditEvent) => void;
    dataKey?: string;

    selectionMode?: "single" | "multiple" | "radiobutton" | "checkbox" | null;
    cellSelection?: boolean;
}

export default function DataTablePrimary<T extends DataTableValueArray = DataTableValueArray>({
    className,
    style,
    emptyMessageTitle = "No hay datos",
    emptyMessageSubtitle = "Realice la búsqueda para visualizar resultados",
    showPaginator = true,
    defaultRows = 10,
    useCustomStyles = false,

    editMode,
    editingRows,
    onRowEditComplete,
    onRowEditChange,
    dataKey,

    selectionMode,
    cellSelection,

    ...props
}: DataTablePrimaryProps<T>) {

    const headerStyle = {
        background: `linear-gradient(to right, ${COLORS.PRIMARY}, ${COLORS.PRIMARY_HOVER})`,
        color: 'white',
        fontSize: '0.875rem',
        fontWeight: 'bold',
        borderRight: '1px solid #e0e0e0',
        whiteSpace: 'pre-line' as const
    };

    const bodyStyle = {
        fontSize: '0.875rem',
        border: '1px solid #e0e0e0',
        whiteSpace: 'pre-line' as const
    };

    const allProps: Record<string, unknown> = {
        ...props,
        stripedRows: true,
        rowHover: true,
        size: "small",
        paginator: showPaginator,
        rows: defaultRows,
        rowsPerPageOptions: [5, 10, 20, 50],
        className: `overflow-hidden rounded-lg shadow-sm ${className || ''}`,
        style: { ...style },
        emptyMessage: (
            <div className="flex flex-col items-center justify-center py-10 text-center">
                <i className="pi pi-info-circle mb-3 text-3xl text-gray-400" />
                <p className="font-semibold text-gray-700">{emptyMessageTitle}</p>
                <p className="text-sm text-gray-500">{emptyMessageSubtitle}</p>
            </div>
        ),
        pt: useCustomStyles ? {
            thead: { style: headerStyle },
            tbody: { style: bodyStyle },
            header: {
                style: {
                    backgroundColor: COLORS.PRIMARY_LIGHT,
                    borderColor: COLORS.PRIMARY,
                    borderWidth: '2px'
                }
            }
        } : {
            header: {
                style: {
                    backgroundColor: COLORS.PRIMARY_LIGHT,
                    borderColor: COLORS.PRIMARY,
                    borderWidth: '2px'
                }
            }
        }
    };

    if (editMode !== undefined) allProps.editMode = editMode;
    if (editingRows !== undefined) allProps.editingRows = editingRows;
    if (onRowEditComplete !== undefined) allProps.onRowEditComplete = onRowEditComplete;
    if (onRowEditChange !== undefined) allProps.onRowEditChange = onRowEditChange;
    if (dataKey !== undefined) allProps.dataKey = dataKey;
    if (selectionMode !== undefined) allProps.selectionMode = selectionMode;
    if (cellSelection !== undefined) allProps.cellSelection = cellSelection;

    return <DataTable {...(allProps as DataTableProps<T>)} />;
}