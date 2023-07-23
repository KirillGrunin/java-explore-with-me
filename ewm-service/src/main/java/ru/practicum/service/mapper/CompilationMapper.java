package ru.practicum.service.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationRequest;
import ru.practicum.model.Compilation;

import java.util.stream.Collectors;

@UtilityClass
public class CompilationMapper {
    public Compilation toCompilation(NewCompilationDto newCompilationDto) {
        final Compilation compilation = new Compilation();
        compilation.setPinned(newCompilationDto.getPinned());
        compilation.setTitle(newCompilationDto.getTitle());
        return compilation;
    }

    public CompilationDto toCompilationDto(Compilation compilation) {
        final CompilationDto compilationDto = new CompilationDto();
        compilationDto.setId(compilation.getId());
        compilationDto.setPinned(compilation.getPinned());
        compilationDto.setTitle(compilation.getTitle());
        if (compilation.getEvents() != null)
            compilationDto.setEvents(compilation.getEvents()
                    .stream()
                    .map(EventMapper::toEventShortDto)
                    .collect(Collectors.toList()));
        return compilationDto;
    }

    public Compilation toCompilation(UpdateCompilationRequest request, Compilation compilation) {
        if (request.getPinned() != null)
            compilation.setPinned(request.getPinned());
        if (request.getTitle() != null)
            compilation.setTitle(request.getTitle());
        return compilation;
    }
}