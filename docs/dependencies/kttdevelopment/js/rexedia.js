const importBtn     = $("#import");
const importFile    = $("#import_file");
const exportBtn     = $("#export");

const coverRegex    = $("#cover-regex");
const coverFormat   = $("#cover-format");
const outputRegex   = $("#output-regex");
const outputFormat  = $("#output-format");
const addRowBtn     = $("#addRow");
const delRowBtn     = $("#deleteRow");

const results       = $("#results");

// regex validation //

const invalid = "is-invalid"
const valid   = "is-valid"

$(document).ready(function(){
    importBtn.click(function(){importFile.click()});
    importFile.change(importPreset);
    exportBtn.click(exportPreset);
    coverRegex.on('input', validateRegex);
    outputRegex.on('input', validateRegex);
    addRowBtn.click(addRow);
    delRowBtn.click(deleteRow);

    window.onbeforeunload = function(){
        if(yml)
            return true
    }

    addRow();

    $(document).on('click', 'button', function(e) {
        e.preventDefault();
    });
})


function validateRegex(e) {
    const target = e.target ? e.target : $(e)[0];
    const regex = target.value
    className = target.className

    if(!regex){
        className = className.replace(' ' + valid, "");
        className = className.replace(' ' + invalid, "");
    }else if(isValidRegex(regex)){
        className = className.replace(' ' + invalid, "");
        if(!className.includes(' ' + valid))
            className += ' ' + valid;
    }else{
        className = className.replace(' ' + valid, "");
        if(!className.includes(' ' + invalid))
            className += ' ' + invalid;
    }

    target.className = className
}

function isValidRegex(string){
    try{
        regex = new RegExp(string);
        return true;
    }catch(e){
        return false;
    }
}

// row handle //

// replace = '#'
const row = `
<div id="#-meta" class="border-bottom mt-2">
    <input type="text" class="form-control form-control-sm mb-2" id="#-meta-name" placeholder="Name">
    <input type="text" class="form-control form-control-sm mb-2" id="#-meta-regex" placeholder="Regex">
    <input type="text" class="form-control form-control-sm mb-2" id="#-meta-format" placeholder="Replacement String">
</div>
`

var rows = 0;

function addRow(){
    rows++;
    $("#rows").append(row.replaceAll('#', rows));
}

function deleteRow(){
    if(rows <= 1)
        return;
    $('#' + rows + "-meta")[0].remove();
    rows--;
}

// tester //

function test(){
    // todo: cover
    // todo: output
    // todo: metadata
}

// import //
var yml;

function importPreset(){
    const file = importFile[0].files[0];
    if(file){
        const IN = new FileReader();
        IN.readAsText(file, "UTF-8");
        IN.onload = function(e){
            yml = jsyaml.load(e.target.result)

            if(yml.cover){
                coverRegex[0].value = yml.cover.regex ? yml.cover.regex : "";
                validateRegex('#' + coverRegex[0].id);
                coverFormat[0].value = yml.cover.format ? yml.cover.format : "";
            }
            if(yml.output){
                outputRegex[0].value = yml.output.regex ? yml.output.regex : "";
                validateRegex('#' + outputRegex[0].id);
                outputFormat[0].value = yml.output.format ? yml.output.format : "";
            }

            var size = rows-1;
            for(var i = 0; i < size; i++) // fixme
                deleteRow();

            var size = yml.metadata.length + 1;
            for(var i = 1; i < size; i++){
                if(i != 1)
                    addRow();
                var row = yml.metadata[i-1];
                $('#' + i + "-meta-name")[0].value   = row.meta ? row.meta : "";
                $('#' + i + "-meta-regex")[0].value  = row.regex ? row.regex : "";
                validateRegex('#' + i + "-meta-regex");
                $('#' + i + "-meta-format")[0].value = row.format ? row.format : "";
            }
            test();
        }
    }
}

// export //

function exportPreset(){
    // todo: parse

    // todo: export
}